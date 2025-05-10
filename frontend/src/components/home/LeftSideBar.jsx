import { Box, Flex, Heading, Spinner, useColorMode, Button} from '@chakra-ui/react';
import '../../index.css';
import { useEffect, useState } from 'react';
import SuggestedOportunity from './SuggestedOportunity';
import { useNavigate } from 'react-router-dom';

const SideBar = () => {
    // Hook para acessar o modo de cor (claro ou escuro)
    const { colorMode } = useColorMode();

    // Estado para armazenar as oportunidades
    const [oportunities, setOportunities] = useState([]);
    console.log(oportunities);

    // Estado para controlar o carregamento dos dados
    const [loading, setLoading] = useState(true);

    // Estado para armazenar mensagens de erro
    const [error, setError] = useState(null);

    const navigate = useNavigate();

    // Efeito colateral para buscar oportunidades sugeridas ao montar o componente
    useEffect(() => {
        const fetchOportunities = async () => {
            try {
                // Requisição para obter oportunidades sugeridas
                const response = await fetch('/api/oportunities/suggested');
                const data = await response.json(); // Extrai os dados da resposta

                // Log da resposta da API para depuração
                console.log('API Response:', data);

                // Atualiza o estado com as oportunidades obtidas
                setOportunities(data || []); // Garante que seja sempre um array

            } catch (err) {
                // Define a mensagem de erro se a requisição falhar
                setError('Failed to fetch opportunities');
                console.error(err); // Log do erro para depuração
            } finally {
                // Define o carregamento como concluído
                setLoading(false);
            }
        };

        fetchOportunities();
    }, []); // O efeito é executado apenas uma vez, após o primeiro render

    if (error) return <p>{error}</p>; // Mensagem de erro

    return (
        <>
            <Flex
                direction={"column"} // Direção dos itens no Flex
                position={"sticky"} // Faz o componente ficar fixo no lado direito
                top={0} // Posiciona o componente no topo
                left={0} // Alinha o componente à esquerda
                width={"23vw"} // Largura da barra lateral
                height={"100vh"} // Altura igual à altura da tela
                overflowY={"auto"} // Adiciona rolagem vertical se necessário
                className={colorMode === "dark" ? 'custom-scrollbar-dark' : 'custom-scrollbar-light'} // Classe CSS para a rolagem
                pt={4} // Padding interno
                zIndex={20} // Coloca o componente acima de outros elementos
                display={{ base: "none", sm: 'none', md: "none", lg: "flex" }} // Exibe o componente apenas em telas grandes
                borderRight={colorMode === "dark" ? "1px solid #343434" : "1px solid #959595"} // Borda direita baseada no modo de cor
            >
                <Box>
                    <Heading size={""} textAlign={"center"} mb={4}>

                        <Button size={"md"} variant={"plain"} onClick={() => {
                            navigate("/oportunities")}   
                        }>
                            Oportunidades
                        </Button>
                    </Heading> {/* Cabeçalho do componente */}
                </Box>

                {/* Mapeia e exibe as oportunidades sugeridas */}
                {Array.isArray(oportunities) && oportunities.map(oportunity => (
                    <SuggestedOportunity
                        key={oportunity._id} // Chave única para cada oportunidade
                        {...oportunity} // Espalha as propriedades da oportunidade
                        oportunity={oportunities} // Passa a lista de oportunidades como prop
                        title={oportunity.title} // Título da oportunidade
                        text={oportunity.text} // Texto da oportunidade
                        postedBy={oportunity.postedBy}
                    />
                ))}

                {loading && (
                    <Flex justify={"center"} w={"23vw"} h={"100%"} alignItems={"center"}>
                        <Spinner size={"xl"} />
                    </Flex>
                )}

            </Flex>


        </>
    );
};

export default SideBar;
