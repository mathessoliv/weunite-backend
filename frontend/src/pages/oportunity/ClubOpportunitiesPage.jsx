import { useState, useEffect } from "react";
import { 
    Box, 
    Flex, 
    Text, 
    VStack, 
    Image, 
    HStack, 
    Icon, 
    Button, 
    useColorModeValue,
    useColorMode,
    Spinner
} from "@chakra-ui/react";
import { FaMapMarkerAlt, FaClock, FaEye } from 'react-icons/fa';
import { useNavigate, useParams } from "react-router-dom"; // Importando useParams
import Header from "../../components/header/Header"; // Importando o cabeçalho da aplicação
import useShowToast from "../../hooks/useShowToast"; // Hook para exibir mensagens
import { useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom"; // Acessando o estado global do usuário

const ClubOportunitiesPage = () => {
    const { club } = useParams(); // Acessando o parâmetro 'club' da URL
    const [opportunities, setOpportunities] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const showToast = useShowToast();
    const navigate = useNavigate();
    const { colorMode } = useColorMode();
    const currentUser = useRecoilValue(userAtom); // Obtendo informações do usuário atual

    const bg = useColorModeValue('gray.200', '#0A0A0A');
    const boxBg = useColorModeValue('gray.100', '#000000');
    const textColor = useColorModeValue('gray.600', 'gray.300');

    useEffect(() => {
        const fetchClubOpportunities = async () => {
            try {
                setIsLoading(true);
                const response = await fetch(`/api/oportunities/user/${club}`); // Usando o parâmetro 'club' na requisição
                const data = await response.json();

                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }

                setOpportunities(data); // Atualizando as oportunidades
            } catch (error) {
                showToast("Error", error.message, "error");
            } finally {
                setIsLoading(false);
            }
        };

        fetchClubOpportunities();
    }, [club, showToast]); // Atualiza as oportunidades sempre que 'club' mudar

    const handleViewApplicants = (opportunityId) => {
        navigate(`/oportunities/${opportunityId}/applicants`);
    };

    const handleEditOpportunity = (opportunityId) => {
        showToast("Info", "Funcionalidade de edição em desenvolvimento", "info");
    };

    const handleDeleteOpportunity = async (opportunityId) => {
        try {
            const response = await fetch(`/api/oportunities/${opportunityId}`, {
                method: 'DELETE',
            });
            const data = await response.json();

            if (data.error) {
                showToast("Error", data.error, "error");
                return;
            }

            setOpportunities(prev => prev.filter(opp => opp._id !== opportunityId)); // Remover a oportunidade da lista
            showToast("Sucesso", "Oportunidade excluída!", "success");
        } catch (error) {
            showToast("Erro", error.message, "error");
        }
    };

    if (isLoading) {
        return (
            <Flex justifyContent="center" alignItems="center" height="100vh">
                <Spinner size="xl" />
            </Flex>
        );
    }

    return (
        <Box bg={bg} minH="100vh">
            <Header />
            <Flex justify="center" p={4}>
                <Box 
                    bg={boxBg} 
                    w="full" 
                    maxW="800px" 
                    borderRadius="lg" 
                    shadow="md" 
                    p={4}
                >
                    <Text fontSize="2xl" fontWeight="bold" mb={6} textAlign="center">
                        Oportunidades do Clube {club}
                    </Text>

                    {opportunities.length === 0 ? (
                        <Flex justifyContent="center" alignItems="center" height="300px">
                            <Text color={textColor} fontSize="lg">
                                Não há oportunidades para este clube.
                            </Text>
                        </Flex>
                    ) : (
                        <VStack spacing={4} width="full">
                            {opportunities.map((opportunity) => (
                                <Flex 
                                    key={opportunity._id} 
                                    width="full" 
                                    bg={colorMode === "dark" ? "#1A1A1A" : "white"} 
                                    borderRadius="md" 
                                    p={4} 
                                    boxShadow="sm"
                                >
                                    <Image 
                                        src={opportunity.img} 
                                        alt={opportunity.title} 
                                        boxSize="64px" 
                                        mr={4} 
                                        objectFit="cover" 
                                        borderRadius="md"
                                    />
                                    <VStack align="flex-start" flex={1} spacing={1}>
                                        <Text fontSize="lg" fontWeight="bold">
                                            {opportunity.title}
                                        </Text>
                                        <HStack spacing={4} color={textColor} fontSize="sm">
                                            <Flex align="center">
                                                <Icon as={FaMapMarkerAlt} mr={1} />
                                                {opportunity.location}
                                            </Flex>
                                            <Flex align="center">
                                                <Icon as={FaClock} mr={1} />
                                                {new Date(opportunity.applicationDeadline).toLocaleDateString()}
                                            </Flex>
                                        </HStack>
                                        <Text color={textColor} fontSize="sm">
                                            Inscrições: {opportunity.applications?.length || 0} / {opportunity.maxApplications}
                                        </Text>
                                        <Flex width="full" justifyContent="space-between" mt={2}>
                                            <Button 
                                                size="sm" 
                                                colorScheme="green"
                                                leftIcon={<FaEye />}
                                                onClick={() => handleViewApplicants(opportunity._id)}
                                            >
                                                Ver Inscritos
                                            </Button>
                                            <HStack>
                                                <Button 
                                                    size="sm" 
                                                    variant="outline"
                                                    onClick={() => handleEditOpportunity(opportunity._id)}
                                                >
                                                    Editar
                                                </Button>
                                                <Button 
                                                    size="sm" 
                                                    colorScheme="red"
                                                    variant="ghost"
                                                    onClick={() => handleDeleteOpportunity(opportunity._id)}
                                                >
                                                    Excluir
                                                </Button>
                                            </HStack>
                                        </Flex>
                                    </VStack>
                                </Flex>
                            ))}
                        </VStack>
                    )}
                </Box>
            </Flex>
        </Box>
    );
};

export default ClubOportunitiesPage;
