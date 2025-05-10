import { Box, Divider, Flex, HStack, Image, Spinner, Text, useColorMode } from "@chakra-ui/react"
import UserHeader from "../../components/header/UserHeader"
import Post from "../../components/user/Post"
import ProfileComment from "../../components/user/ProfileComment"
import { useEffect, useState } from "react"
import { Link, useParams } from "react-router-dom"
import useShowToast from '../../hooks/useShowToast' // Hook para exibir mensagens de toast
import useGetUserProfile from "../../hooks/useGetUserProfile" // Hook para obter o perfil do usuário
import { useRecoilState } from "recoil"
import postsAtom from "../../atoms/postsAtom" // Átomo para gerenciar o estado dos posts
import commentsAtom from "../../atoms/commentsAtom" // Átomo para gerenciar o estado dos posts
import oportunitiesAtom from "../../atoms/oportunitiesAtom"
import { Link as RouterLink } from 'react-router-dom';
import { Pointer } from "lucide-react"
import { MotionValue } from "framer-motion"


const UserPage = () => {
  // Obtém o perfil do usuário e o estado de carregamento
  const { user, loading } = useGetUserProfile();
  // Obtém o nome de usuário da URL
  const { username } = useParams();
  // Função para exibir mensagens de toast
  const showToast = useShowToast();
  // Estado para armazenar os posts e função para atualizar o estado
  const [posts, setPosts] = useRecoilState(postsAtom);
  // Estado para controlar o carregamento dos posts
  const [fetchingPosts, setFetchingPosts] = useState(true);

  const [comments, setComments] = useRecoilState(commentsAtom);

  const [fetchingComments, setFetchingComments] = useState(true);

  const [activeTab, setActiveTab] = useState('posts');

  const [fetchingOpportunities, setFetchingOpportunities] = useState(true);

  const [opportunities, setOpportunities] = useRecoilState(oportunitiesAtom);

  const { colorMode } = useColorMode(); // Mudei

  useEffect(() => {
    // Função assíncrona para buscar os posts do usuário
    const getPosts = async () => {
      // Se não houver um usuário, não faz a requisição
      if (!user) return;
      setFetchingPosts(true); // Define o estado de carregamento como verdadeiro
      try {
        // Faz a requisição para obter os posts do usuário
        const res = await fetch(`/api/posts/user/${username}`);
        const data = await res.json();
        setPosts(data); // Atualiza o estado dos posts
      } catch (error) {
        // Exibe mensagem de erro e limpa o estado dos posts em caso de falha
        showToast("Error", error.message, "error");
        setPosts([]);
      } finally {
        setFetchingPosts(false); // Define o estado de carregamento como falso
      }
    }
    // Chama a função para obter os posts
    getPosts();
  }, [username, showToast, setPosts, user]); // Dependências do useEffect

  useEffect(() => {
    // Função assíncrona para buscar os posts do usuário
    const getComments = async () => {
      // Se não houver um usuário, não faz a requisição
      if (!user) return;
      setFetchingComments(true); // Define o estado de carregamento como verdadeiro
      try {
        // Faz a requisição para obter os posts do usuário
        const res = await fetch(`/api/posts/user/${username}/comments`);
        const data = await res.json();
        setComments(data); // Atualiza o estado dos posts
      } catch (error) {
        // Exibe mensagem de erro e limpa o estado dos posts em caso de falha
        showToast("Error", error.message, "error");
        setComments([]);
      } finally {
        setFetchingComments(false); // Define o estado de carregamento como falso
      }
    }
    // Chama a função para obter os posts
    getComments();
  }, [username, showToast, setComments, user]); // Dependências do useEffect

  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };

  useEffect(() => {
    const getOpportunities = async () => {
      if (!user) return;
      setFetchingOpportunities(true);
      try {
        const res = await fetch(`/api/oportunities/user/${username}`);
        const data = await res.json();
        setOpportunities(data);
      } catch (error) {
        showToast("Error", error.message, "error");
        setOpportunities([]);
      } finally {
        setFetchingOpportunities(false);
      }
    };
    getOpportunities();
  }, [username, showToast, setOpportunities, user]);

  // Se o usuário não está carregado e está no processo de carregamento
  if (!user && loading) {
    return (
      <Flex justifyContent={"center"} alignItems={"center"} minH={"100vh"}>
        <Spinner size={"xl"} /> {/* Exibe o spinner enquanto carrega */}
      </Flex>
    )
  }

  // Se o usuário não foi encontrado e não está carregando
  if (!user && !loading) return <h1>Usuário não encontrado</h1>;


  return (
    <>
      <Flex w={"100vw"} h={"100vh"} justifyContent={"center"}>
        <Flex w={{ base: "100vw", sm: '100vw', md: "90vw", lg: '50vw' }} flexDirection={"column"}>
          {/* Exibe o cabeçalho do usuário */}
          <UserHeader user={user} username={username} />

          <Flex>
            <Flex
              //Mudei
              flex={1}
              justifyContent={"center"}
              fontWeight={'bold'}
              onClick={() => handleTabChange('posts')}
              color={activeTab === 'posts' ? (colorMode === "light" ? '#000000' : '#ffffff') : '#343434'}
              borderBottom={activeTab === 'posts' ? (colorMode === "light" ? '2px solid #000000' : '2px solid #ffffff') : '1px solid #343434'}
            >
              <Text mb={3} cursor={"pointer"} >Publicações</Text>
            </Flex>

            <Flex
              //Mudei
              flex={1}
              justifyContent={"center"}
              fontWeight={'bold'}
              onClick={() => handleTabChange('comments')}
              color={activeTab === 'comments' ? (colorMode === "light" ? '#000000' : '#ffffff') : '#959595'}
              borderBottom={activeTab === 'comments' ? (colorMode === "light" ? '2px solid #000000' : '2px solid #ffffff') : '1px solid #343434'}
            >
              <Text mb={3} cursor={"pointer"}>Comentários</Text>
            </Flex>

            {opportunities.length > 0 && (
              <Flex
                //Mudei
                flex={1}
                justifyContent={"center"}
                fontWeight={'bold'}
                onClick={() => handleTabChange('opportunities')}
                color={activeTab === 'opportunities' ? (colorMode === "light" ? '#000000' : '#ffffff') : '#959595'}
                borderBottom={activeTab === 'opportunities' ? (colorMode === "light" ? '2px solid #000000' : '2px solid #ffffff') : '1px solid #343434'}>
                <Text mb={3} cursor={"pointer"}>Oportunidades</Text>
              </Flex>
            )}
          </Flex>

          {!fetchingPosts && activeTab === 'posts' && posts.length === 0 && (
            <Text textAlign="center" mt={2}>O usuário não tem nenhuma publicação</Text>
          )}

          {!fetchingComments && activeTab === 'comments' && comments.length === 0 && (
            <Text textAlign="center" mt={2}>O usuário não tem nenhum comentário</Text>
          )}

          {!fetchingOpportunities && activeTab === 'opportunities' && opportunities.length === 0 && (
            <Text textAlign="center" mt={2}>O usuário não tem nenhuma oportunidade publicada</Text>
          )}

          {activeTab === 'posts' && posts.map((post) => (
            <Post key={post._id} post={post} postedBy={post.postedBy} />
          ))}

          {activeTab === 'comments' && comments.map((comment) => (
            <ProfileComment key={comment.commentId} user={user} comment={comment} />
          ))}

          {activeTab === 'opportunities' && opportunities.map((opportunity) => (
               
            <Box
              key={opportunity._id}
              cursor="pointer"
              p={3}
              border={colorMode === "light" ? '' : '1px solid #101010'} //Mudei
              borderRadius="md"
              shadow="lg"
              mt={2}
              mb={4}
              //Mudei
              bg={colorMode === "light" ? 'gray.100' : '#000000'}
            >
              <Flex bg={colorMode === "light" ? 'gray.100' : '#000000'} direction={{ base: 'column', md: 'row' }} justify="space-between" align="start">
                <HStack spacing={4} mb={{ base: 4, md: 0 }}>
                  <Image boxSize="70px" src={opportunity.img || '/images/default.png'} alt={opportunity.title} borderRadius="md" />
                  <Box>
                    <Text fontSize="lg" fontWeight="bold" mb={1}>{opportunity.title}</Text>
                    <Text fontSize="md" mb={1}>
                      {user?.username} - {opportunity.location || 'Local não informado'}
                    </Text>
                    <Text fontSize="sm" color="gray" mb={1}>
                      {opportunity.text}
                    </Text>
                    <Text fontSize="sm" color="gray" mb={1}>
                      Prazo de candidatura: {new Date(opportunity.applicationDeadline).toLocaleDateString() || 'Não informado'}
                    </Text>
                    <Text fontSize="sm" color="gray" mb={1}>
                      Publicado em: {new Date(opportunity.createdAt).toLocaleDateString() || 'Data não informada'}
                    </Text>
                  </Box>
                </HStack>
              </Flex>
              <Divider my={4} />
            </Box>
           
          ))}


        </Flex>
      </Flex>
    </>
  )
}

export default UserPage
