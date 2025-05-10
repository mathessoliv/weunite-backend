/* eslint-disable react/prop-types */
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useSocket } from '../../context/SocketContext';
import { useRecoilValue } from 'recoil';
import userAtom from '../../atoms/userAtom';
import {
    Drawer,
    DrawerBody,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    useDisclosure,
    Button,
    Flex,
    Avatar,
    Text,
    Heading,
    Divider,
    IconButton,
    useColorMode,
    Badge,
    Box,
    useToast,
    VStack,
} from '@chakra-ui/react';
import { IoMdNotificationsOutline } from "react-icons/io";
import { formatDistanceToNow } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const NotificationMessage = ({ notification, colorMode }) => {
    const getNotificationText = (type) => {
        switch (type) {
            case 'like':
                return 'curtiu sua publicação';
            case 'comment':
                return 'comentou em sua publicação';
            case 'follow':
                return 'começou a seguir você';
            default:
                return notification.message || 'interagiu com você';
        }
    };

    return (
        <Flex
            alignItems="flex-start"
            gap={3}
            p={3}
            borderRadius="sm"
            w="full"
            bg={!notification.isRead ? (colorMode === "dark" ? "whiteAlpha.100" : "gray.50") : "transparent"}
            transition="background-color 0.2s"
            cursor="pointer"
        >
            <Avatar
                size="md"
                src={notification.triggeredBy?.profilePic}
                name={notification.triggeredBy?.username}
            />
            <Box flex="1">
                <Flex alignItems="baseline" gap={2}>
                    <Heading size="sm" fontWeight="600">
                        {notification.triggeredBy?.username}
                    </Heading>
                    <Text fontSize="sm" color={colorMode === "dark" ? "gray.400" : "gray.600"}>
                        {getNotificationText(notification.type)}
                    </Text>
                </Flex>
                <Text fontSize="xs" color={colorMode === "dark" ? "gray.500" : "gray.600"} mt={1}>
                    {formatDistanceToNow(new Date(notification.createdAt), {
                        addSuffix: true,
                        locale: ptBR
                    })}
                </Text>
            </Box>
            {!notification.isRead && (
                <Box
                    w="2"
                    h="2"
                    borderRadius="full"
                    bg="blue.500"
                    alignSelf="center"
                />
            )}
        </Flex>
    );
};

export const Notificacao = () => {
    const { colorMode } = useColorMode();
    const { isOpen, onOpen, onClose } = useDisclosure();
    const btnRef = React.useRef();
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const { socket } = useSocket();
    const user = useRecoilValue(userAtom);
    const toast = useToast();

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const { data } = await axios.get('/api/notifications');
            setNotifications(data);
            const unread = data.filter(notif => !notif.isRead).length;
            setUnreadCount(unread);
        } catch (error) {
            toast({
                title: "Erro ao carregar notificações",
                description: error.response?.data?.error || "Ocorreu um erro inesperado",
                status: "error",
                duration: 3000,
                isClosable: true,
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchNotifications(); // Carregar notificações ao montar

        // Configurar listeners do socket
        if (socket) {
            socket.on("newNotification", (notification) => {
                setNotifications(prev => [notification, ...prev]);
                setUnreadCount(prev => prev + 1); // Atualiza a contagem de notificações não lidas

                // Mostrar toast para nova notificação
                toast({
                    title: "Nova notificação",
                    description: `${notification.triggeredBy.username} ${notification.message}`,
                    status: "info",
                    duration: 5000,
                    isClosable: true,
                    position: "top-right"
                });
            });

            socket.on("notificationsRead", () => {
                setUnreadCount(0);
                setNotifications(prev =>
                    prev.map(notif => ({ ...notif, isRead: true }))
                );
            });
        }

        return () => {
            if (socket) {
                socket.off("newNotification");
                socket.off("notificationsRead");
            }
        };
    }, [socket, toast]);

    const handleMarkAsRead = async () => {
        try {
            await axios.put('/api/notifications/read');
            if (socket) {
                socket.emit("markNotificationsAsRead", user?._id);
            }
            setUnreadCount(0);
            setNotifications(prev =>
                prev.map(notif => ({ ...notif, isRead: true }))
            );
            toast({
                title: "Sucesso",
                description: "Todas as notificações foram marcadas como lidas",
                status: "success",
                duration: 3000,
                isClosable: true,
            });
        } catch (error) {
            toast({
                title: "Erro",
                description: "Não foi possível marcar as notificações como lidas",
                status: "error",
                duration: 3000,
                isClosable: true,
            });
        }
    };

    const handleNotificationClick = async (notification) => {
        if (!notification.isRead) {
            try {
                await axios.put(`/api/notifications/${notification._id}/read`);
                setNotifications(prev =>
                    prev.map(n =>
                        n._id === notification._id ? { ...n, isRead: true } : n
                    )
                );
                setUnreadCount(prev => Math.max(0, prev - 1));
            } catch (error) {
                console.error('Erro ao marcar notificação como lida:', error);
            }
        }
    };

    return (
        <>
            <IconButton
                fontSize={{ base: "15px", md: "20px", lg: "25px" }}
                onClick={onOpen}
                variant="ghost"
                position="relative"
                icon={
                    <Box position="relative">
                        <IoMdNotificationsOutline />
                        {unreadCount > 0 && (
                            <Badge
                                position="absolute"
                                top="-2"
                                right="-2"
                                colorScheme="red"
                                borderRadius="full"
                                minW="1.5rem"
                                h="1.5rem"
                                display="flex"
                                alignItems="center"
                                justifyContent="center"
                                fontSize="xs"
                            >
                                {unreadCount > 99 ? '99+' : unreadCount}
                            </Badge>
                        )}
                    </Box>
                }
                aria-label="Notificações"
            />
            <Drawer
                isOpen={isOpen}
                placement='right'
                onClose={onClose}
                finalFocusRef={btnRef}
                size="md"
            >
                <DrawerOverlay />
                <DrawerContent  bg={colorMode === "dark" ? "#000000" : "gray.100"} border={colorMode === "dark" ? "2px solid #343434" : ""}>
                    <DrawerCloseButton />
                    <DrawerHeader>
                        <Flex justify="space-between" align="center">
                            <Text>Notificações</Text>
                            {unreadCount > 0 && (
                                <Badge colorScheme="red" borderRadius="full" px={2}>
                                    {unreadCount} nova{unreadCount !== 1 && 's'}
                                </Badge>
                            )}
                        </Flex>
                    </DrawerHeader>
                    <Divider borderColor={colorMode === "dark" ? "#949494" : "gray.400"} />
                    <DrawerBody p={0}>
                        {loading ? (
                            <Flex justify="center" align="center" h="full">
                                <Text>Carregando notificações...</Text>
                            </Flex>
                        ) : notifications.length === 0 ? (
                            <Flex justify="center" align="center" h="full">
                                <Text color="gray.500">Nenhuma notificação</Text>
                            </Flex>
                        ) : (
                            <VStack spacing={0} align="stretch">
                                {notifications.map((notification) => (
                                    <Box
                                        key={notification._id}
                                        onClick={() => handleNotificationClick(notification)}
                                    >
                                        <NotificationMessage
                                            notification={notification}
                                            colorMode={colorMode}
                                        />
                                    </Box>
                                ))}
                            </VStack>
                        )}
                    </DrawerBody>
                    <DrawerFooter>
                        <Button
                            w="full"
                            onClick={handleMarkAsRead}
                            isDisabled={unreadCount === 0}
                            colorScheme="blue"
                        >
                            Marcar todas como lidas
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    );
};

export default Notificacao;
