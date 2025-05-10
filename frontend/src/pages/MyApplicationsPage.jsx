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
import { FaMapMarkerAlt, FaClock } from 'react-icons/fa';
import { useParams, useNavigate } from "react-router-dom";
import Header from "../components/header/Header";
import useShowToast from "../hooks/useShowToast";
import { useRecoilValue } from "recoil";
import userAtom from "../atoms/userAtom";

const MyApplicationsPage = () => {
    const [applications, setApplications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const { username } = useParams();
    const showToast = useShowToast();
    const navigate = useNavigate();
    const { colorMode } = useColorMode();
    const currentUser = useRecoilValue(userAtom);

    const bg = useColorModeValue('gray.200', '#0A0A0A');
    const boxBg = useColorModeValue('gray.100', '#000000');
    const textColor = useColorModeValue('gray.600', 'gray.300');

    useEffect(() => {
        const fetchApplications = async () => {
            try {
                setIsLoading(true);
                const response = await fetch(`/api/oportunities/applied-oportunities/${currentUser.username}`);
                const data = await response.json();

                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }

                setApplications(data);
            } catch (error) {
                showToast("Error", error.message, "error");
            } finally {
                setIsLoading(false);
            }
        };

        fetchApplications();
    }, [currentUser.username, showToast]);

    const handleCancelApplication = async (oid) => {
        try {
            const response = await fetch(`/api/oportunities/apply/${oid}`, {
                method: "POST",
            });
            const data = await response.json();

            if (data.error) {
                showToast("Error", data.error, "error");
                return;
            }

            // Remove the application from the list
            setApplications(prev => prev.filter(opp => opp._id !== oid));
            showToast("Sucesso", "Inscrição cancelada!", "success");
        } catch (error) {
            showToast("Erro", error.message, "error");
        }
    };

    const handleViewDetails = (oid) => {
        navigate(`/oportunity/oportunities/${oid}`);
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
                        Minhas Inscrições
                    </Text>

                    {applications.length === 0 ? (
                        <Flex justifyContent="center" alignItems="center" height="300px">
                            <Text color={textColor} fontSize="lg">
                                Você não tem inscrições em nenhuma oportunidade.
                            </Text>
                        </Flex>
                    ) : (
                        <VStack spacing={4} width="full">
                            {applications.map((oportunity) => (
                                <Flex 
                                    key={oportunity._id} 
                                    width="full" 
                                    bg={colorMode === "dark" ? "#1A1A1A" : "white"} 
                                    borderRadius="md" 
                                    p={4} 
                                    boxShadow="sm"
                                >
                                    <Image 
                                        src={oportunity.img} 
                                        alt={oportunity.title} 
                                        boxSize="64px" 
                                        mr={4} 
                                        objectFit="cover" 
                                        borderRadius="md"
                                    />
                                    <VStack align="flex-start" flex={1} spacing={1}>
                                        <Text fontSize="lg" fontWeight="bold">
                                            {oportunity.title}
                                        </Text>
                                        <HStack spacing={4} color={textColor} fontSize="sm">
                                            <Flex align="center">
                                                <Icon as={FaMapMarkerAlt} mr={1} />
                                                {oportunity.location}
                                            </Flex>
                                            <Flex align="center">
                                                <Icon as={FaClock} mr={1} />
                                                {new Date(oportunity.applicationDeadline).toLocaleDateString()}
                                            </Flex>
                                        </HStack>
                                        <Text color={textColor} fontSize="sm">
                                            Inscrições: {oportunity.applications?.length || 0} / {oportunity.maxApplications}
                                        </Text>
                                        <Flex width="full" justifyContent="space-between" mt={2}>
                                            <Button 
                                                size="sm" 
                                                variant="outline" 
                                                onClick={() => handleViewDetails(oportunity._id)}
                                            >
                                                Ver Detalhes
                                            </Button>
                                            <Button 
                                                size="sm" 
                                                colorScheme="red" 
                                                variant="ghost"
                                                onClick={() => handleCancelApplication(oportunity._id)}
                                            >
                                                Cancelar Inscrição
                                            </Button>
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

export default MyApplicationsPage;