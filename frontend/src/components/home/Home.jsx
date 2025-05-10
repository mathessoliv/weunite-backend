import { Flex, Spinner } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import useShowToast from "../../hooks/useShowToast";
import PostHome from "./PostHome";
import { useRecoilState } from "recoil";
import postsAtom from "../../atoms/postsAtom";

// Componente funcional para a página inicial
const Home = () => {
    // Estado global para posts e estado local para carregamento
    const [posts, setPosts] = useRecoilState(postsAtom);
    const [loading, setLoading] = useState(true);

    // Hook personalizado para mostrar notificações
    const showToast = useShowToast();

    useEffect(() => {
        // Função assíncrona para buscar posts do feed
        const getFeedPosts = async () => {
            // Reseta o estado dos posts e define o carregamento como verdadeiro
            setPosts([]);
            setLoading(true);

            try {
                // Faz a requisição para a API
                const res = await fetch("/api/posts/feed");
                const data = await res.json();

                // Verifica se há um erro na resposta
                if (data.error) {
                    showToast("Erro", data.error, "error");
                    return;
                }

                // Atualiza o estado dos posts com os dados recebidos
                setPosts(data);
            } catch (error) {
                // Exibe uma mensagem de erro caso ocorra uma exceção
                showToast("Erro", error.message, "error");
            } finally {
                // Define o carregamento como falso após a conclusão da requisição
                setLoading(false);
            }
        }

        // Chama a função para buscar os posts ao montar o componente
        getFeedPosts();
    }, [showToast, setPosts]); // Dependências do useEffect

    return (
        <>
            {/* Exibe uma mensagem caso não haja posts e o carregamento tenha terminado */}
            {!loading && posts.length === 0 && (
                <Flex width={"52vw"} justifyContent={"center"}  ml={{base: 120, sm: 150, md: 180, lg: 0}} mt={200}>
                    Siga usuários para ver seus posts
                </Flex>
            )}

            {/* Exibe um indicador de carregamento enquanto os posts estão sendo carregados */}
            {loading && (
                <Flex justify={"center"} w={"100%"} h={"100%"} alignItems={"center"} mt={200}>
                    <Spinner size={"xl"} />
                </Flex>
            )}

            {/* Exibe os posts quando o carregamento terminar e houver posts disponíveis */}
            <Flex direction={"column"} flex={1} className={"feed-scrollbar feed-container"}>
                {posts.map((post) => (
                    <PostHome key={post._id} post={post} postedBy={post.postedBy} />
                ))}
            </Flex>
        </>
    );
};

export default Home;
