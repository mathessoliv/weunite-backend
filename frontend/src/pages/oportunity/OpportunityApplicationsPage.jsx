import { useState, useEffect } from "react";
import { 
    Box, 
    Flex, 
    Text, 
    VStack, 
    Image, 
    HStack, 
    Avatar,
    Button, 
    useColorModeValue,
    useColorMode,
    Spinner,
    Tag
} from "@chakra-ui/react";
import { useParams, useNavigate } from "react-router-dom";
import Header from "../../components/header/Header";
import useShowToast from "../../hooks/useShowToast";
import { useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";

const OpportunityApplicationsPage = () => {
    const [opportunity, setOpportunity] = useState(null);
    const [applications, setApplications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const { oid } = useParams();
    const showToast = useShowToast();
    const navigate = useNavigate();
    const { colorMode } = useColorMode();
    const currentUser = useRecoilValue(userAtom);

    const bg = useColorModeValue('gray.200', '#0A0A0A');
    const boxBg = useColorModeValue('gray.100', '#000000');
    const textColor = useColorModeValue('gray.600', 'gray.300');

    useEffect(() => {
        const fetchOpportunityApplications = async () => {
            try {
                setIsLoading(true);
                // Fetch opportunity details
                const opportunityResponse = await fetch(`/api/oportunities/${oid}`);
                const opportunityData = await opportunityResponse.json();

                if (opportunityData.error) {
                    showToast("Error", opportunityData.error, "error");
                    return;
                }

                // Fetch applications details
                const applicationsResponse = await fetch(`/api/oportunities/opportunity-applications/${oid}`);
                const applicationsData = await applicationsResponse.json();

                if (applicationsData.error) {
                    showToast("Error", applicationsData.error, "error");
                    return;
                }

                setOpportunity(opportunityData);
                setApplications(applicationsData);
            } catch (error) {
                showToast("Error", error.message, "error");
            } finally {
                setIsLoading(false);
            }
        };

        fetchOpportunityApplications();
    }, [oid, showToast]);

    const handleViewProfile = (username) => {
        navigate(`/${username}`);
    };

    if (isLoading) {
        return (
            <Flex justifyContent="center" alignItems="center" height="100vh">
                <Spinner size="xl" />
            </Flex>
        );
    }

    if (!opportunity) {
        return null;
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
                    <Flex mb={6} alignItems="center">
                        <Image 
                            src={opportunity.img} 
                            alt={opportunity.title} 
                            boxSize="64px" 
                            mr={4} 
                            objectFit="cover" 
                            borderRadius="md"
                        />
                        <VStack align="flex-start" spacing={1}>
                            <Text fontSize="2xl" fontWeight="bold">
                                {opportunity.title}
                            </Text>
                            <Tag colorScheme="green">
                                {applications.length} / {opportunity.maxApplications} Vagas Preenchidas
                            </Tag>
                        </VStack>
                    </Flex>

                    {applications.length === 0 ? (
                        <Flex justifyContent="center" alignItems="center" height="300px">
                            <Text color={textColor} fontSize="lg">
                                Nenhuma inscrição para esta oportunidade.
                            </Text>
                        </Flex>
                    ) : (
                        <VStack spacing={4} width="full">
                            {applications.map((application) => (
                                <Flex 
                                    key={application._id} 
                                    width="full" 
                                    bg={colorMode === "dark" ? "#1A1A1A" : "white"} 
                                    borderRadius="md" 
                                    p={4} 
                                    boxShadow="sm"
                                    alignItems="center"
                                >
                                    <Avatar 
                                        src={application.profilePic} 
                                        name={application.username} 
                                        mr={4} 
                                        size="md" 
                                    />
                                    <VStack align="flex-start" flex={1} spacing={1}>
                                        <Text fontSize="lg" fontWeight="bold">
                                            {application.username}
                                        </Text>
                                        <Text color={textColor} fontSize="sm">
                                            {application.email}
                                        </Text>
                                    </VStack>
                                    <Button 
                                        size="sm" 
                                        variant="outline" 
                                        onClick={() => handleViewProfile(application.username)}
                                    >
                                        Ver Perfil
                                    </Button>
                                </Flex>
                            ))}
                        </VStack>
                    )}
                </Box>
            </Flex>
        </Box>
    );
};

export default OpportunityApplicationsPage;