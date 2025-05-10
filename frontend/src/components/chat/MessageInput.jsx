/* eslint-disable react/prop-types */
import {
	Box,
	Button,
	CloseButton,
	Flex,
	FormControl,
	Image,
	Input,
	InputGroup,
	InputRightElement,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	// useDisclosure,
	useColorMode,
	useDisclosure,
} from "@chakra-ui/react";
import { useRef, useState } from "react";
import { IoSendSharp } from "react-icons/io5";
import useShowToast from "../../hooks/useShowToast";
import { conversationsAtom, selectedConversationAtom } from "../../atoms/messagesAtom";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { BsFillImageFill } from "react-icons/bs";
import usePreviewMedia from "../../hooks/usePreviewMedia";
import { FaVideo } from "react-icons/fa";

const MessageInput = ({ setMessages }) => {
	const { colorMode } = useColorMode();
	const [messageText, setMessageText] = useState("");
	const showToast = useShowToast();
	const selectedConversation = useRecoilValue(selectedConversationAtom);
	const setConversations = useSetRecoilState(conversationsAtom);
	const mediaRef = useRef(null);
	// const { onClose } = useDisclosure();
	const { handleMediaChange, mediaUrl, setMediaUrl, mediaType, setMediaType } = usePreviewMedia();
	const [isSending, setIsSending] = useState(false);
	const { isOpen, onOpen, onClose } = useDisclosure();

	const handleSendMessage = async (e) => {
		e.preventDefault();
		if (!messageText && !mediaUrl) return;
		if (isSending) return;

		setIsSending(true);

		try {
			const res = await fetch("/api/messages", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					message: messageText,
					recipientId: selectedConversation.userId,
					mediaUrl,
					mediaType
				}),
			});
			const data = await res.json();
			if (data.error) {
				showToast("Error", data.error, "error");
				return;
			}
			setMessages((messages) => [...messages, data]);

			setConversations((prevConvs) => {
				const updatedConversations = prevConvs.map((conversation) => {
					if (conversation._id === selectedConversation._id) {
						return {
							...conversation,
							lastMessage: {
								text: messageText,
								sender: data.sender,
							},
						};
					}
					return conversation;
				});
				return updatedConversations;
			});
			setMessageText("");
			setMediaUrl("");
			setMediaType('none');
		} catch (error) {
			showToast("Error", error.message, "error");
		} finally {
			setIsSending(false);
		}
	};


	return (
		<Flex gap={2} alignItems={"center"}>
			<form onSubmit={handleSendMessage} style={{ flex: 95 }}>
				<InputGroup>
					<Input
						w={"full"}
						placeholder='Digite uma mensagem'
						onChange={(e) => setMessageText(e.target.value)}
						value={messageText}
						border={`1px solid ${colorMode === "light" ? "#000000" : "gray"}`}
					/>
					<InputRightElement onClick={handleSendMessage} cursor={"pointer"}>
						<IoSendSharp />
					</InputRightElement>
				</InputGroup>
			</form>
			<Flex flex={5} cursor={"pointer"}>
				<BsFillImageFill size={20} onClick={onOpen}/>
				<Input
					type="file"
					hidden
					ref={mediaRef}
					onChange={handleMediaChange}
					accept="image/*,video/*"
				/>
			</Flex>

			<Modal isOpen={isOpen} onClose={onClose}>
				<ModalOverlay />
				<ModalContent
					bg={colorMode === "dark" ? "black" : "gray.100"}
					color={colorMode === "dark" ? "white" : "black"}
				>
					<ModalHeader>Selecionar Mídia</ModalHeader>
					<ModalCloseButton />
					<ModalBody pb={6}>
						<FormControl>

							<Input
								type="file"
								hidden
								ref={mediaRef}
								onChange={handleMediaChange}
								accept="image/*,video/*"
							/>
							<Flex gap={2} alignItems="center">
								<BsFillImageFill
									style={{ cursor: "pointer" }}
									size={16}
									onClick={() => mediaRef.current.click()}
								/>
								<FaVideo
									style={{ cursor: "pointer" }}
									size={16}
									onClick={() => mediaRef.current.click()}
								/>
							</Flex>
						</FormControl>

						{mediaUrl && (
							<Flex mt={5} width={"full"} pos={"relative"}>
								{mediaType === 'image' ? (
									<Image src={mediaUrl} alt="Mídia selecionada" />
								) : mediaType === 'video' ? (
									<Box width="full">
										<video
											src={mediaUrl}
											controls
											style={{ width: '100%' }}
										/>
									</Box>
								) : null}
								<CloseButton
									onClick={() => {
										setMediaUrl("");
										setMediaType('none');
									}}
									pos={"absolute"}
									top={2}
									right={2}
								/>
							</Flex>
						)}
					</ModalBody>

					<ModalFooter>
						<Button
							color="#000000"
							mr={3}
							onClick={handleSendMessage}
							isLoading={isSending}
							bg={"#03C03C"}
							_hover={{ opacity: 0.8 }}
							transition={0.3}
						>
							Confirmar
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Flex>
	);
};

export default MessageInput;