/* eslint-disable react/prop-types */
import { useEffect, useState } from 'react';
import {
  Avatar,
  Box,
  Flex,
  Image,
  Text,
  useColorMode,
  IconButton,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  Button,
} from "@chakra-ui/react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { formatDistanceToNow } from "date-fns";
import { useRecoilState, useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";
import postsAtom from "../../atoms/postsAtom";
import Actions from "../videos/Actions";
import CustomVideoPlayer from "../videos/CustomVideoPlayer";
import { MoreVertical, Share2, Flag } from 'lucide-react';
import useShowToast from "../../hooks/useShowToast";
import { ptBR } from 'date-fns/locale';

const Post = ({ post, postedBy }) => {
  const { colorMode } = useColorMode();
  const [user, setUser] = useState(null);
  const currentUser = useRecoilValue(userAtom);
  const [posts, setPosts] = useRecoilState(postsAtom);
  const navigate = useNavigate();
  const showToast = useShowToast();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const getUser = async () => {
      try {
        const res = await fetch("/api/users/profile/" + postedBy);
        const data = await res.json();

        if (data.error) {
          showToast("Erro", data.error, "error");
          return;
        }
        setUser(data);
      } catch (error) {
        showToast("Erro", error.message, "error");
        setUser(null);
      }
    };

    getUser();
  }, [postedBy, showToast]);

  const handleDeletePost = async () => {
    if (!window.confirm("Tem certeza que deseja excluir esta publicação?")) return;

    try {
      setIsDeleting(true);
      const res = await fetch(`/api/posts/${post._id}`, {
        method: "DELETE",
      });
      const data = await res.json();

      if (data.error) {
        showToast("Erro", data.error, "error");
        return;
      }

      showToast("Sucesso", "Publicação excluída", "success");
      setPosts(posts.filter((p) => p._id !== post._id));
    } catch (error) {
      showToast("Erro", error.message, "error");
    } finally {
      setIsDeleting(false);
    }
  };

  const handleSharePost = () => {
    const postUrl = `${window.location.origin}/${user?.username}/post/${post._id}`;
    navigator.clipboard.writeText(postUrl);
    showToast("Sucesso", "Link copiado para a área de transferência", "success");
  };

  const handleReportPost = () => {
    onOpen();
  };

  const renderMedia = () => {
    if (post.mediaType === 'image' && post.img) {
      return (
        <Image
          src={post.img}
          alt='Imagem do post'
          borderRadius="lg"
          mt={4}
          w="full"
          objectFit="cover"
        // transition="transform 0.3s ease"
        // _hover={{
        //   transform: 'scale(1.01)'
        // }}
        />
      );
    } else if (post.mediaType === 'video' && post.video) {
      return (
        <Box mt={4} width="100%">
          <CustomVideoPlayer src={post.video} />
        </Box>
      );
    }
    return null;
  };

  if (!user) return null;

  return (
    <Box
      width={"100%"}
      flexDirection={"column"}
      //bg={colorMode === "dark" ? "#0000000" : "gray.100"} //Mudei
      p={5}
      // shadow= {colorMode === "dark" ? " 0px 0px 10px 0px rgba(20,20,20,1)" : "md"} //Mudei
      borderBottom="1px solid"
      borderColor={colorMode === "dark" ? "#343434" : "gray"}
      mt={2}
    >
      <Flex justifyContent="space-between" alignItems="center" mb={4}>
        <Flex alignItems="center" gap={3}>
          <Avatar
            src={user?.profilePic}
            name={user?.username}
            size="md"
            cursor="pointer"
            onClick={() => navigate(`/${user.username}`)}
          />
          <Box>
            <Text
              fontWeight="bold"
              cursor="pointer"
              onClick={() => navigate(`/${user?.username}`)}
              _hover={{ color: "#03C03C" }}
            >
              {user?.username}
            </Text>
            <Text fontSize="sm" color="gray.500">
              {formatDistanceToNow(new Date(post.createdAt), {
                addSuffix: true,
                locale: ptBR
              })}
            </Text>
          </Box>
        </Flex>

        <Menu>
          <MenuButton
            as={IconButton}
            icon={<MoreVertical size={20} />}
            variant="ghost"
            rounded="full"
            size="sm"
            _hover={{ bg: colorMode === "dark" ? "whiteAlpha.200" : "gray.100" }}
          />
          <MenuList bg={colorMode === "dark" ? "#000000": "gray.100"}>
            <MenuItem  bg={colorMode === "dark" ? "#000000": "gray.100"} icon={<Share2 size={16} />} onClick={handleSharePost}>
              Compartilhar
            </MenuItem>
            {currentUser?._id === user?._id ? (
              <MenuItem
                color="red.500"
                onClick={handleDeletePost}
                isDisabled={isDeleting}
                bg={colorMode === "dark" ? "#000000": "gray.100"}
              >
                Excluir publicação
              </MenuItem>
            ) : (
              <MenuItem bg={colorMode === "dark" ? "#000000": "gray.100"} icon={<Flag size={16} />} onClick={handleReportPost}>
                Reportar
              </MenuItem>
            )}
          </MenuList>
        </Menu>
      </Flex>

      <Box>
        <Text
          fontSize="md"
          mb={post.mediaType !== 'none' ? 2 : 4}
          color={colorMode === "dark" ? "gray.100" : "gray.800"}
          as={RouterLink}
          to={`/${user?.username}/post/${post._id}`}
          style={{ textDecoration: 'none' }}
          _hover={{ textDecoration: 'none' }}
        >
          {post.text}
        </Text>
        {renderMedia()}
      </Box>

      <Actions post={post} />

      {/* Modal de Denúncia */}
      <Modal isOpen={isOpen} onClose={onClose} isCentered>
        <ModalOverlay />
        <ModalContent bg={colorMode === "dark" ? "gray.800" : "white"}>
          <ModalHeader>Reportar Publicação</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <Text mb={4}>Por que você quer denunciar esta publicação?</Text>
            <Flex direction="column" gap={2}>
              <Button variant="ghost" justifyContent="start">
                Conteúdo inadequado
              </Button>
              <Button variant="ghost" justifyContent="start">
                Spam
              </Button>
              <Button variant="ghost" justifyContent="start">
                Assédio
              </Button>
              <Button variant="ghost" justifyContent="start">
                Informação falsa
              </Button>
              <Button variant="ghost" justifyContent="start">
                Outro motivo
              </Button>
            </Flex>
          </ModalBody>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default Post;