import { Box, Flex, Heading, Spinner, useColorMode } from "@chakra-ui/react";
import useShowToast from "../../hooks/useShowToast";
import { useEffect, useState } from "react";
import SuggestedUser from "./SuggestedUser";

// Componente de barra lateral direita
const RightSideBar = () => {
    // Hook para acessar o modo de cor (claro ou escuro)
    const { colorMode } = useColorMode();

    // Estado para controlar o carregamento dos dados
    const [loading, setLoading] = useState(true);

    // Estado para armazenar os usuários sugeridos
    const [suggestedUsers, setSuggestedUsers] = useState([]);

    // Função personalizada para mostrar toast
    const showToast = useShowToast();

    // Efeito colateral para buscar usuários sugeridos ao montar o componente
    useEffect(() => {
        const getSuggestedUsers = async () => {
            setLoading(true); // Inicia o carregamento
            try {
                // Requisição para obter usuários sugeridos
                const res = await fetch("/api/users/suggested");
                const data = await res.json();

                // Se houver um erro na resposta, exibe um toast com a mensagem de erro
                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }

                // Atualiza o estado com os usuários sugeridos
                setSuggestedUsers(data);
            } catch (error) {
                // Exibe um toast em caso de exceção
                showToast("Error", error.message, "error");
            } finally {
                setLoading(false); // Finaliza o carregamento
            }
        };

        getSuggestedUsers();
    }, [showToast]);

    return (
        <Flex
            flexDirection={"column"} // Direção dos itens no Flex
            position={"sticky"} // Faz o componente ficar fixo no lado direito
            top={0} // Posiciona o componente no topo
            right={0} // Alinha o componente à direita
            width={{ base: "30vw", md: "23vw" }} // Largura responsiva
            height={"100vh"} // Altura igual à altura da tela
            overflowY={"scroll"} // Adiciona rolagem vertical se necessário
            className={colorMode === "dark" ? 'custom-scrollbar-dark' : 'custom-scrollbar-light'} // Classe CSS para a rolagem
            p={4} // Padding interno
            zIndex={20} // Coloca o componente acima de outros elementos
            borderLeft={colorMode === "dark" ? "1px solid #343434" : "1px solid #959595"} // Borda esquerda baseada no modo de cor
            bg={colorMode === "dark" ? "#0A0A0A" : "gray.200"} // Cor de fundo baseada no modo de cor
            display={{ base: "none", sm: 'none', md: "none", lg: "flex" }} // Exibe o componente apenas em telas grandes
        >
            <Box mb={4}>
                <Heading size={"md"} textAlign={"center"}>Sugestões</Heading> {/* Cabeçalho do componente */}
            </Box>

            {/* Mapeia e exibe os usuários sugeridos se não estiver carregando */}
            
            {!loading && suggestedUsers.map((user) => <SuggestedUser key={user._id} user={user} />)}

            {loading && (
                <Flex justify={"center"} w={"23vw"} h={"100%"} alignItems={"center"}>
                    <Spinner size={"xl"} />
                </Flex>
            )}
        </Flex>
    );
};

export default RightSideBar;