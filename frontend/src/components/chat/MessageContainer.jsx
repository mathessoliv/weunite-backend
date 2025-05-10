import { Avatar, Divider, Flex, Text, useColorModeValue, SkeletonCircle, Skeleton, useColorMode } from '@chakra-ui/react';
import { useEffect, useRef, useState } from 'react';
import Message from './Message';
import MessageInput from './MessageInput';
import useShowToast from '../../hooks/useShowToast';
import { useRecoilState, useRecoilValue } from 'recoil';
import { conversationsAtom, selectedConversationAtom } from '../../atoms/messagesAtom';
import userAtom from '../../atoms/userAtom';
import { useSocket } from '../../context/SocketContext';

const MessageContainer = () => {

  const { colorMode } = useColorMode(); //Mudei
  const showToast = useShowToast();
  const selectedConversation = useRecoilValue(selectedConversationAtom);
  const [loadingMessages, setLoadingMessages] = useState(true);
  const [messages, setMessages] = useState([]);
  const currentUser = useRecoilValue(userAtom);
  const { socket } = useSocket();
  const [conversations, setConversations] = useRecoilState(conversationsAtom);
  const messageEndRef = useRef(null);

  useEffect(() => {
    socket.on("newMessage", (message) => {
      if (selectedConversation._id === message.conversationId) {
        setMessages((prev) => [...prev, message]);
      }

      // make a sound if the window is not focused
      if (!document.hasFocus()) {
        const sound = new Audio(messageSound);
        sound.play();
      }

      setConversations((prev) => {
        const updatedConversations = prev.map((conversation) => {
          if (conversation._id === message.conversationId) {
            return {
              ...conversation,
              lastMessage: {
                text: message.text,
                sender: message.sender,
              },
            };
          }
          return conversation;
        });
        return updatedConversations;
      });
    });

    return () => socket.off("newMessage");
  }, [socket, selectedConversation, setConversations]);

  useEffect(() => {
    const lastMessageIsFromOtherUser = messages.length && messages[messages.length - 1].sender !== currentUser._id;
    if (lastMessageIsFromOtherUser) {
      socket.emit("markMessagesAsSeen", {
        conversationId: selectedConversation._id,
        userId: selectedConversation.userId,
      });
    }

    socket.on("messagesSeen", ({ conversationId }) => {
      if (selectedConversation._id === conversationId) {
        setMessages((prev) => {
          const updatedMessages = prev.map((message) => {
            if (!message.seen) {
              return {
                ...message,
                seen: true,
              };
            }
            return message;
          });
          return updatedMessages;
        });
      }
    });
  }, [socket, currentUser._id, messages, selectedConversation]);

  useEffect(() => {
    messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    const getMessages = async () => {
      setLoadingMessages(true);
      setMessages([]);
      try {
        if (selectedConversation.mock) return;
        const res = await fetch(`/api/messages/${selectedConversation.userId}`);
        const data = await res.json();
        if (data.error) {
          showToast("Error", data.error, "error");
          return;
        }
        setMessages(data);
      } catch (error) {
        showToast("Error", error.message, "error");
      } finally {
        setLoadingMessages(false);
      }
    };

    getMessages();
  }, [showToast, selectedConversation.userId, selectedConversation.mock]);

  return (
    <Flex
      flex="70"
      bg={useColorModeValue("gray.100", "#000000")} //Mudei
      borderRadius="lg" shadow="md" //Mudei
      flexDirection="column"
      p={2}
      height="100%"
      maxW="full"
    >

      <Flex w="full" h={12} alignItems="center" gap={2}>
        <Avatar src={selectedConversation.userProfilePic} size="sm" />
        <Text display="flex" alignItems="center">
          {selectedConversation.username}
          {/*Removi imagem sem uso */}
        </Text>
      </Flex>

      <Divider borderColor={colorMode === "dark" ? "#949494" : "#000000"} /> {/*Mudei */}


      <Flex
        flexDirection="column"
        gap={4}
        flex="1"
        overflowY="auto"
        maxH="calc(100% - 120px)"
        p={2}
        sx={{
          '&::-webkit-scrollbar': {
            width: '6px',
          },
          '&::-webkit-scrollbar-thumb': {
            background: useColorModeValue('gray.400', 'gray.600'),
            borderRadius: '10px',
          },
          '&::-webkit-scrollbar-thumb:hover': {
            background: useColorModeValue('gray.500', 'gray.700'),
          },
          '&::-webkit-scrollbar-track': {
            background: useColorModeValue('gray.300', 'gray.900'),
          },
        }}
      >
        {loadingMessages ? (
          [...Array(5)].map((_, i) => (
            <Flex
              key={i}
              gap={2}
              alignItems="center"
              p={1}
              borderRadius="md"
              alignSelf={i % 2 === 0 ? "flex-start" : "flex-end"}
            >
              {i % 2 === 0 && <SkeletonCircle size={7} />}
              <Flex flexDir="column" gap={2}>
                <Skeleton h='8px' w='250px' />
                <Skeleton h='8px' w='250px' />
                <Skeleton h='8px' w='250px' />
              </Flex>
              {i % 2 !== 0 && <SkeletonCircle size={7} />}
            </Flex>
          ))
        ) : (
          messages.map((message, index) => (
            <Flex
              key={message._id}
              alignSelf={currentUser._id === message.sender ? "flex-end" : "flex-start"}
              ref={index === messages.length - 1 ? messageEndRef : null}
            >
              <Message message={message} ownMessage={currentUser._id === message.sender} />
            </Flex>
          ))
        )}
      </Flex>


      <MessageInput setMessages={setMessages} />
    </Flex>
  );
};

export default MessageContainer;