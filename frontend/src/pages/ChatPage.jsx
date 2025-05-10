import { SearchIcon, ArrowBackIcon } from "@chakra-ui/icons"; //Mudei
import { Box, Button, Flex, Input, Skeleton, SkeletonCircle, Text, useColorModeValue, useColorMode } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import Conversation from "../components/chat/Conversation";
import { GiConversation } from "react-icons/gi";
import Header from "../components/header/Header";
import MessageContainer from "../components/chat/MessageContainer";
import useShowToast from "../hooks/useShowToast";
import { useRecoilState, useRecoilValue } from "recoil";
import { conversationsAtom, selectedConversationAtom } from "../atoms/messagesAtom";
import userAtom from "../atoms/userAtom";
import { useSocket } from "../context/SocketContext";

const ChatPage = () => {
    const [searchingUser, setSearchingUser] = useState(false);
    const [loadingConversations, setLoadingConversations] = useState(true);
    const [searchText, setSearchText] = useState("");
    const [selectedConversation, setSelectedConversation] = useRecoilState(selectedConversationAtom);
    const [conversations, setConversations] = useRecoilState(conversationsAtom);
    const currentUser = useRecoilValue(userAtom);
    const showToast = useShowToast();
    const { socket, onlineUsers } = useSocket();

    useEffect(() => {
        socket?.on("messagesSeen", ({ conversationId }) => {
            setConversations((prev) => {
                const updatedConversations = prev.map((conversation) => {
                    if (conversation._id === conversationId) {
                        return {
                            ...conversation,
                            lastMessage: {
                                ...conversation.lastMessage,
                                seen: true,
                            },
                        };
                    }
                    return conversation;
                });
                return updatedConversations;
            });
        });
    }, [socket, setConversations]);

    const { colorMode } = useColorMode();

    useEffect(() => {
        const getConversations = async () => {
            try {
                const res = await fetch("/api/messages/conversations");
                const data = await res.json();
                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }
                console.log(data);
                setConversations(data);
            } catch (error) {
                showToast("Error", error.message, "error");
            } finally {
                setLoadingConversations(false);
            }
        };
        getConversations();
    }, [showToast, setConversations]);

    const handleConversationSearch = async (e) => {
        e.preventDefault();
        setSearchingUser(true);
        try {
            const res = await fetch(`/api/users/profile/${searchText}`);
            const searchedUser = await res.json();
            if (searchedUser.error) {
                showToast("Error", searchedUser.error, "error");
                return;
            }
            const messagingYourself = searchedUser._id === currentUser._id
            if (messagingYourself) {
                showToast("Error", "Você não pode enviar mensagem para você mesmo!", "error");
                return;
            }

            const conversationAlreadyExists = conversations.find((conversation) => conversation.participants[0]._id === searchedUser._id)
            if (conversationAlreadyExists) {
                setSelectedConversation({
                    _id: conversations._id,
                    userId: searchedUser._id,
                    username: searchedUser.username,
                    userProfilePic: searchedUser.profilePic,
                });
                return;
            }
            const mockConversation = {
                mock: true,
                lastMessage: {
                    text: "",
                    sender: "",
                },
                _id: Date.now(),
                participants: [
                    {
                        _id: searchedUser._id,
                        username: searchedUser.username,
                        profilePic: searchedUser.profilePic,
                    },
                ],
            };
            setConversations((prevConvs) => [...prevConvs, mockConversation]);
        } catch (error) {
            showToast("Error", error.message, "error");
        } finally {
            setSearchingUser(false);
        }
    };


    //Mudei
    const handleBackClick = () => {
        setSelectedConversation({});
    };

    return (
        <Flex direction="column" minH="100vh">
            <Header />
            <Flex w={"100vw"} h={"auto"} justifyContent={"center"} alignItems={"center"}>
                <Flex boxShadow={colorMode === "dark" ? "0 2px 4px rgba(77,77,77,1)" : "0 2px 4px rgba(92,92,92,1)"}
                     w={"80vw"} h={"80vh"}>
                    <Flex
                        gap={4}
                        flexDirection={{ base: "column", md: "row" }}
                        maxW={{ sm: "400px", md: "full" }}
                        mx="auto"
                        flex={1}
                        p={4}

                    >
                        <Flex flex={30} gap={2} flexDirection="column" maxW={{ sm: "250px", md: "full" }} mx={"auto"}
                            display={{ base: selectedConversation._id ? "none" : "flex", md: "flex" }} //Mudei
                        >
                            <Text fontWeight={700} color={useColorModeValue("#000000", "#FFFFFF")}>
                                Suas Conversas
                            </Text>
                            <form onSubmit={handleConversationSearch}>
                                <Flex alignItems={"center"} gap={2}>
                                    <Input placeholder="Procure por usuários" onChange={(e) => setSearchText(e.target.value)}
                                        border="1px"
                                        borderColor={useColorModeValue("#000000", "#343434")}
                                    />
                                    <Button size={"sm"} onClick={handleConversationSearch} variant="ghost" isLoading={searchingUser}>
                                        <SearchIcon />
                                    </Button>
                                </Flex>
                            </form>
                            {loadingConversations &&
                                [0, 1, 2, 3, 4].map((_, i) => (
                                    <Flex key={i} gap={4} alignItems={"center"} p={"1"} borderRadius={"md"}>
                                        <Box>
                                            <SkeletonCircle size={"10"} />
                                        </Box>
                                        <Flex w={"full"} flexDirection={"column"} gap={3}>
                                            <Skeleton h={"10px"} w={"80px"} />
                                            <Skeleton h={"8px"} w={"90%"} />
                                        </Flex>
                                    </Flex>
                                ))}
                            {!loadingConversations && onlineUsers && (
                                conversations.map(conversation => (
                                    <Conversation
                                        key={conversation._id}
                                        isOnline={onlineUsers.includes(conversation.participants[0]._id)}
                                        conversation={conversation}
                                    />
                                ))
                            )}
                        </Flex>
                        {!selectedConversation._id ? (
                            <Flex
                                flex={70}
                                borderRadius={"md"}
                                p={2}
                                flexDir={"column"}
                                alignItems={"center"}
                                justifyContent={"center"}
                                display={{ base: selectedConversation._id ? "flex" : "none", md: "flex" }} //Mudei
                                height={{
                                    base: "100px",
                                    sm: "300px",
                                    md: "500px",
                                    lg: "700px",
                                }}
                            >
                                <Flex
                                    flex={70}
                                    p={4}
                                    flexDirection="column"
                                    maxW="100%"
                                    h="100%"
                                    overflow="hidden"

                                >
                                    <GiConversation size={100} />
                                    <Text fontSize={20}>Selecione uma Conversa para iniciar mensagens</Text>
                                </Flex>
                            </Flex>
                        ) : (

                            //Mudei
                            <Flex flex={70} flexDirection="column" overflowY="auto">
                                <Button
                                    onClick={handleBackClick}
                                    leftIcon={<ArrowBackIcon />}
                                    display={{ base: "flex", md: "none" }}
                                    mb={4}
                                >
                                    Voltar
                                </Button>
                                <MessageContainer flex={1} />
                            </Flex>

                        )}
                    </Flex>
                </Flex>
            </Flex>
        </Flex>
    );
};

export default ChatPage;