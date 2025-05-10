/* eslint-disable react/prop-types */

import { useState, useEffect } from "react";
import { useRecoilValue } from "recoil";
import { Link as RouterLink } from "react-router-dom";
import {
  Avatar, AvatarBadge, Box, Button, Flex, IconButton, Link, Modal, ModalBody, ModalCloseButton, ModalContent, ModalHeader, ModalOverlay,
  Stack, Text, useDisclosure, useColorMode,
  Spinner
} from "@chakra-ui/react";
import { IoHomeOutline } from "react-icons/io5";
import { FiEdit2 } from "react-icons/fi";
import userAtom from '../../atoms/userAtom';
import useShowToast from '../../hooks/useShowToast';

const UserHeader = ({ user, username }) => {
  const currentUser = useRecoilValue(userAtom);
  const showToast = useShowToast();
  const [followers, setFollowers] = useState([]);
  const [followingUsers, setFollowingUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [following, setFollowing] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [followerCount, setFollowerCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);
  const { colorMode } = useColorMode(); //Mudei

  const { isOpen: isFollowersOpen, onOpen: onFollowersOpen, onClose: onFollowersClose } = useDisclosure();
  const { isOpen: isFollowingOpen, onOpen: onFollowingOpen, onClose: onFollowingClose } = useDisclosure();

  useEffect(() => {
    if (currentUser && user) {
      setFollowing(user.followers.includes(currentUser._id));
    }
  }, [currentUser, user]);

  useEffect(() => {
    const fetchFollowersAndFollowing = async () => {
      setIsLoading(true);
      try {
        const [followersResponse, followingResponse] = await Promise.all([
          fetch(`/api/users/followers/${username}`),
          fetch(`/api/users/following/${username}`)
        ]);

        if (!followersResponse.ok || !followingResponse.ok) {
          throw new Error('Failed to fetch followers or following');
        }

        const followersData = await followersResponse.json();
        const followingData = await followingResponse.json();

        setFollowers(followersData);
        setFollowingUsers(followingData);
        setFollowerCount(followersData.length);
        setFollowingCount(followingData.length);
      } catch (err) {
        console.error(err);
        showToast("Error", "Failed to fetch followers and following", "error");
      } finally {
        setIsLoading(false);
      }
    };

    fetchFollowersAndFollowing();
  }, [username, showToast]);

  const handleFollowUnfollow = async () => {
    if (!currentUser) {
      showToast("Error", "Please login to follow", "error");
      return;
    }
    if (updating) return;

    setUpdating(true);
    try {
      const res = await fetch(`/api/users/follow/${user._id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });
      const data = await res.json();
      if (data.error) {
        showToast("Error", data.error, "error");
        return;
      }

      setFollowing(!following);
      setFollowingCount(prev => following ? prev - 1 : prev + 1);

      // Update followers list
      if (following) {
        setFollowers(prev => prev.filter(follower => follower._id !== currentUser._id));
      } else {
        setFollowers(prev => [...prev, currentUser]);
      }

    } catch (error) {
      showToast("Error", error.message, "error");
    } finally {
      setUpdating(false);
    }
  };

  if (isLoading) return <>
    <Flex w={"100%"} height={300} alignItems={"center"} justifyContent={"center"}>
      <Spinner></Spinner>
    </Flex>
  </>;

  return (
    <>
      <Flex w={"100%"}>
        <Stack flex={1}>

          {/* Ícone de início visível apenas em telas maiores */}
          <Box display={{ base: "none", md: "flex" }} justifyContent={"center"} p={{ base: 4 }}>
            <Link as={RouterLink} to={"/"}>
              <IconButton bg={"transparent"} icon={<IoHomeOutline size={26} cursor={"pointer"} />} />
            </Link>
          </Box>

          <Box pt={{ base: 5 }} display={"flex"} justifyContent={"space-between"} alignItems={"center"}>
            <Box display={"flex"} alignItems={"center"} gap={4}>
              {/* Exibe o avatar do usuário ou um avatar padrão se não houver imagem de perfil */}
              {user.profilePic ? (
                <Avatar name={user.name} src={user.profilePic} size={{ base: "lg", md: "xl" }} cursor={"pointer"}>
                  {currentUser?._id === user._id && (
                    <AvatarBadge boxSize="1.25em" bg="#000000" border="1px solid #343434" display={{ base: 'flex', sm: 'flex', md: 'flex', lg: 'none' }}>
                      <Link as={RouterLink} to="/update">
                        <FiEdit2 style={{ fontSize: '0.5em' }} />
                      </Link>
                    </AvatarBadge>
                  )}
                </Avatar>
              ) : (
                <Avatar name={user.username} src="https://bit.ly/broken-link" size={{ base: "lg", md: "xl" }} cursor={"pointer"}>
                  {currentUser?._id === user._id && (
                    <AvatarBadge boxSize="1.25em" backgroundColor={"#000000"} border="1px solid #343434" display={{ base: 'flex', sm: 'flex', md: 'flex', lg: 'none' }}>
                      <Link as={RouterLink} to="/update">
                        <FiEdit2 style={{ fontSize: '0.5em' }} />
                      </Link>
                    </AvatarBadge>
                  )}
                </Avatar>
              )}

              <Box display={"flex"} gap={3}>
                <Box display={"flex"} flexDirection={"column"}>
                  {/* Nome e nome de usuário do usuário */}
                  <Text fontWeight={"bold"}>{user.name}</Text>
                  <Text fontSize={"md"} color={"#959595"}>{user.username}</Text>
                </Box>

                {/* Botão para editar o perfil ou seguir/deixar de seguir */}
                {currentUser?._id === user._id ? (
                  <Box display={{ base: 'none', sm: 'none', md: 'none', lg: 'flex' }} alignItems={"center"}>
                    <Link as={RouterLink} to="/update">
                      <Button variant={"outline"}
                        border="1px solid" // Mudei
                        borderColor={colorMode === "dark" ? "#343434" : "gray.300"} // Mudei
                        _hover={{
                          borderColor: "#03C03C",
                          bg: colorMode === "dark" ? "#000000" : "gray.200", //Mudei
                          cursor: "pointer",
                        }}>
                        Editar perfil
                      </Button>
                    </Link>
                  </Box>
                ) : (
                  <Box display={"flex"} alignItems={"center"} mr={{ base: 9 }}>
                    <Button variant={"outline"} onClick={handleFollowUnfollow} isLoading={updating}
                      border="1px solid" // Mudei
                      borderColor={colorMode === "dark" ? "#343434" : "gray.300"} // Mudei
                      _hover={{
                        borderColor: "#03C03C",
                        bg: colorMode === "dark" ? "#000000" : "gray.200" //Mudei
                      }}
                    >
                      {following ? "Remover" : "Seguir"}
                    </Button>
                  </Box>
                )}
              </Box>
            </Box>

            <Box display={"flex"} gap={{ base: '3', sm: '3', md: '10' }} textAlign={"center"} alignItems={"center"}>
              <Box>
                <Text fontWeight={"bold"} fontSize={"md"}>Seguindo</Text>
                <Text onClick={onFollowersOpen} cursor={"pointer"}>{followerCount}</Text>
              </Box>
              <Box>
                <Text fontWeight={"bold"} fontSize={"md"}>Seguidores</Text>
                <Text onClick={onFollowingOpen} cursor={"pointer"}>{followingCount}</Text>
              </Box>
            </Box>
          </Box>


          {/* Seção da bio do usuário */}
          {user.bio ? (
            <Box mt={5} pb={6}>
              <Text color={"#959595"} fontSize={"lg"} mb={2} fontWeight={"bold"}>Bio</Text>
              <Text fontSize={"md"}>{user.bio}</Text>
            </Box>
          ) : (
            <Box mt={5} pb={6}>

            </Box>
          )}


          {/* Seção de publicações e comentários */}

        </Stack>

      </Flex>

      {/* Modal para exibir os seguidores */}

      <Modal isOpen={isFollowersOpen} onClose={onFollowersClose} scrollBehavior={'inside'}>
        <ModalOverlay />
        <ModalContent
          bg={colorMode === "dark" ? "black" : "white"} // Mudei
          color={colorMode === "dark" ? "white" : "black"} // Mudei
        >
          <ModalHeader>Seguindo</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            {followers.length === 0 ? (
              <Box display={"flex"} justifyContent={"center"} alignItems={"center"} width={"100%"} minH={150}>
                <Text>Esse usuário não segue ninguém</Text>
              </Box>
            ) : (
              <Box>
                {followers.map((follower) => (
                  <Link key={follower._id} as={RouterLink} to={`/${follower.username}`} style={{ textDecoration: "none" }}>
                    <Box display={"flex"} gap={3} alignItems={"center"} py={3}
                      _hover={{
                        backgroundColor: colorMode === "dark" ? "#121212" : "gray.200", //Mudei
                        borderRadius: "md"
                      }}
                      p={1}
                    >
                      <Avatar src={follower.profilePic} alt={follower.username} border={"1px solid #343434"}/>
                      <Box>
                        <Text fontWeight={"bold"}>{follower.name}</Text>
                        <Text>{follower.username}</Text>
                      </Box>
                    </Box>
                  </Link>
                ))}
              </Box>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>

      <Modal isOpen={isFollowingOpen} onClose={onFollowingClose} scrollBehavior={'inside'}>
        <ModalOverlay />
        <ModalContent
          bg={colorMode === "dark" ? "black" : "white"} // Mudei
          color={colorMode === "dark" ? "white" : "black"} // Mudei
        >
          <ModalHeader>Seguidores</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            {followingUsers.length === 0 ? (
              <Box display={"flex"} justifyContent={"center"} alignItems={"center"} width={"100%"} minH={150}>
                <Text>Esse usuário não possui seguidores</Text>
              </Box>
            ) : (
              <Box>
                {followingUsers.map((followedUser) => (
                  <Link key={followedUser._id} as={RouterLink} to={`/${followedUser.username}`} style={{ textDecoration: "none" }}>
                    <Box display={"flex"} gap={3} alignItems={"center"} py={3}
                      _hover={{
                        backgroundColor: colorMode === "dark" ? "#121212" : "gray.200", //Mudei
                        borderRadius: "md"
                      }}
                      p={1}
                      >
                      <Avatar src={followedUser.profilePic} alt={followedUser.username} border={"1px solid #343434"} />
                      <Box>
                        <Text fontWeight={"bold"}>{followedUser.name}</Text>
                        <Text>{followedUser.username}</Text>
                      </Box>
                    </Box>
                  </Link>
                ))}
              </Box>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  );
};

export default UserHeader;
