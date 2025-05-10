import { AddIcon } from "@chakra-ui/icons";
import {
    Button,
    CloseButton,
    Flex,
    FormControl,
    Image,
    Input,
    Modal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalFooter,
    ModalHeader,
    ModalOverlay,
    Text,
    Textarea,
    useColorMode,
    useDisclosure,
    Box
} from "@chakra-ui/react";
import { useRef, useState } from "react";
import usePreviewMedia from "../../hooks/usePreviewMedia";
import { BsFillImageFill } from "react-icons/bs";
import { FaVideo } from "react-icons/fa";
import { useRecoilState, useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";
import useShowToast from "../../hooks/useShowToast";
import postsAtom from "../../atoms/postsAtom";
import { useParams } from "react-router-dom";

const MAX_CHAR = 500;

const CreatePost = () => {
    const { colorMode } = useColorMode();
    const { isOpen, onOpen, onClose } = useDisclosure();
    const [postText, setPostText] = useState('');
    const { handleMediaChange, mediaUrl, setMediaUrl, mediaType, setMediaType } = usePreviewMedia();
    const mediaRef = useRef(null);
    const [remainingChar, setRemainingChar] = useState(MAX_CHAR);
    const user = useRecoilValue(userAtom);
    const showToast = useShowToast();
    const [updating, setUpdating] = useState(false);
    const [posts, setPosts] = useRecoilState(postsAtom);
    const { username } = useParams();

    const handleTextChange = (e) => {
        const inputText = e.target.value;
        if (inputText.length > MAX_CHAR) {
            const truncatedText = inputText.slice(0, MAX_CHAR);
            setPostText(truncatedText);
            setRemainingChar(0);
        } else {
            setPostText(inputText);
            setRemainingChar(MAX_CHAR - inputText.length);
        }
    };

    const handleCreatePost = async () => {
        setUpdating(true);
        try {
            const res = await fetch("/api/posts/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    postedBy: user._id,
                    text: postText,
                    mediaUrl,
                    mediaType
                }),
            });

            const data = await res.json();
            if (data.error) {
                showToast("Error", data.error, "error");
                return;
            }
            showToast("Successo", "Publicação feita com sucesso", "success");

            if (username === user.username) {
                setPosts([data, ...posts]);
            }

            onClose();
            setPostText("");
            setMediaUrl("");
            setMediaType('none');
        } catch (error) {
            showToast("Error", error.message, "error");
        } finally {
            setUpdating(false);
        }
    };

    return (
        <>
            <Button
                position={"fixed"}
                bottom={10}
                right={10}
                bg={colorMode === "dark" ? "#000000" : "#f8fafc"}
                variant={"outline"}
                _hover={{
                    borderColor: "#03C03C",
                    bg: colorMode === "dark" ? "#000000" : "#FFFFFF"
                }}
                zIndex={50}
                onClick={onOpen}
            >
                <AddIcon />
            </Button>

            <Modal isOpen={isOpen} onClose={onClose}>
                <ModalOverlay />
                <ModalContent
                    bg={colorMode === "dark" ? "black" : "gray.100"}
                    color={colorMode === "dark" ? "white" : "black"}
                >
                    <ModalHeader>Criar publicação</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody pb={6}>
                        <FormControl>
                            <Textarea
                                placeholder="Conteúdo da publicação"
                                onChange={handleTextChange}
                                value={postText}
                                focusBorderColor="#03C03C"
                            />
                            <Text
                                fontSize={"xs"}
                                fontWeight={"bold"}
                                textAlign={"right"}
                                m={1}
                                color={"#343434"}
                            >
                                {remainingChar}/{MAX_CHAR}
                            </Text>

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
                            onClick={handleCreatePost}
                            isLoading={updating}
                            bg={"#03C03C"}
                            _hover={{ opacity: 0.8 }}
                            transition={0.3}
                        >
                            Publicar
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
        </>
    );
};

export default CreatePost;