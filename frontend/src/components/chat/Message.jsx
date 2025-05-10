/* eslint-disable react/prop-types */
import { Avatar, Box, Flex, Image, Skeleton, Text, useColorModeValue,useColorMode } from "@chakra-ui/react";
import { selectedConversationAtom } from "../../atoms/messagesAtom";
import { useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";
import { BsCheck2All } from "react-icons/bs";
import { useState } from "react";
import CustomVideoPlayer from "../videos/CustomVideoPlayer";

const Message = ({ ownMessage, message }) => {
	const selectedConversation = useRecoilValue(selectedConversationAtom);
	const user = useRecoilValue(userAtom);
	const [imgLoaded, setImgLoaded] = useState(false);
	const { colorMode } = useColorMode();
	
	return (
		<>
			{ownMessage ? (
				<Flex gap={2} alignSelf={"flex-end"}>
					{message.text && (
						<Flex bg={colorMode === "dark" ? ('green.600', 'green.800'):  ('green.600', 'green.600') } maxW={"350px"} p={1} borderRadius={"md"}>
							<Text color={"white"}>{message.text}</Text>
							<Box alignSelf={"flex-end"} ml={1} color={message.seen ? "blue.400" : ""} fontWeight={"bold"}>
								<BsCheck2All size={16} />
							</Box>
						</Flex>
					)}
					{message.img && !imgLoaded && (
						<Flex mt={5} w={"200px"}>
							<Image
								src={message.img}
								hidden
								onLoad={() => setImgLoaded(true)}
								alt='Message image'
								borderRadius={4}
							/>
							<Skeleton w={"200px"} h={"200px"} />
						</Flex>
					)}
					{message.img && imgLoaded && (
						<Flex mt={5} w={"200px"}>
							<Image src={message.img} alt='Message image' borderRadius={4} />
							<Box alignSelf={"flex-end"} ml={1} color={message.seen ? "blue.400" : ""} fontWeight={"bold"}>
								<BsCheck2All size={16} />
							</Box>
						</Flex>
					)}
					{message.video && (
						<Flex mt={5} w={"500px"} height={"500px"}>
							<CustomVideoPlayer src={message.video} />
							<Box alignSelf={"flex-end"} ml={1} color={message.seen ? "blue.400" : ""} fontWeight={"bold"}>
								<BsCheck2All size={16} />
							</Box>
						</Flex>
					)}
					<Avatar src={user.profilePic} w='7' h={7} />
				</Flex>
			) : (
				<Flex gap={2}>
					<Avatar src={selectedConversation.userProfilePic} w='7' h={7} />
					{message.text && (
						<Text maxW={"350px"} bg={"gray.400"} p={1} borderRadius={"md"} color={"black"}>
							{message.text}
						</Text>
					)}
					{message.img && !imgLoaded && (
						<Flex mt={5} w={"200px"}>
							<Image
								src={message.img}
								hidden
								onLoad={() => setImgLoaded(true)}
								alt='Message image'
								borderRadius={4}
							/>
							<Skeleton w={"200px"} h={"200px"} />
						</Flex>
					)}
					{message.img && imgLoaded && (
						<Flex mt={5} w={"200px"}>
							<Image src={message.img} alt='Message image' borderRadius={4} />
						</Flex>
					)}
					{message.video && (
						<Flex mt={5} w={"500px"} h={500}>
							<CustomVideoPlayer src={message.video} />
						</Flex>
					)}
				</Flex>
			)}
		</>
	);
};

export default Message;
