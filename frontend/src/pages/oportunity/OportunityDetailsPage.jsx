import { useState, useEffect } from "react";
import { Box, Button, Flex, HStack, Icon, Image, Tag, Text, useColorModeValue, VStack,useColorMode } from "@chakra-ui/react"
import { useParams } from "react-router-dom";
import { useRecoilState, useRecoilValue } from "recoil";
import useShowToast from "../../hooks/useShowToast";
import useGetUserProfile from "../../hooks/useGetUserProfile";
import postsAtom from "../../atoms/postsAtom";
import userAtom from "../../atoms/userAtom";
import Header from "../../components/header/Header";
import { FaMapMarkerAlt, FaClock } from 'react-icons/fa';

const OportunityDetailsPage = () => {
    const { user } = useGetUserProfile();
    const [oportunities, setOportunities] = useRecoilState(postsAtom);
    const showToast = useShowToast();
    const { oid } = useParams();
    const currentUser = useRecoilValue(userAtom);
    const { colorMode} = useColorMode();
    const bg = useColorModeValue('gray.200', '#0A0A0A'); //Mudei
    const boxBg = useColorModeValue('gray.100', '#000000'); //Mudei
    const textColor = useColorModeValue('gray.600', 'gray.300');

    const [isApplied, setIsApplied] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const currentOportunity = oportunities[0];

    useEffect(() => {
        const getPost = async () => {
            setOportunities([]);
            try {
                const res = await fetch(`/api/oportunities/${oid}`);
                const data = await res.json();
                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }
                setOportunities([data]);
                setIsApplied(data.applications?.includes(currentUser._id));
            } catch (error) {
                showToast("Error", error.message, "error");
            }
        };
        getPost();
    }, [showToast, oid, setOportunities, currentUser._id]);

    const handleApply = async () => {
        if (!currentUser) {
            showToast("Erro", "Você deve estar logado para se inscrever!", "error");
            return;
        }

        if (currentOportunity?.postedBy === currentUser._id) {
            showToast("Erro", "Você não pode se insrcever na sua própria oportunidade", "error");
            return;
        }

        if (currentOportunity?.applications?.length >= currentOportunity?.maxApplications) {
            showToast("Erro", "O limite de inscrições para esta oportunidade foi atingido", "error");
            return;
        }

        setIsLoading(true);

        try {
            const res = await fetch(`/api/oportunities/apply/${oid}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
            });
            const data = await res.json();

            if (data.error) {
                showToast("Error", data.error, "error");
            } else {
                setIsApplied(data.applied);
                showToast(
                    "Sucesso",
                    data.applied ? "Inscriçao concluída!" : "Desinscrição concluída!",
                    "success"
                );
                // Update the opportunities state
                setOportunities(prev => {
                    return prev.map(opp => {
                        if (opp._id === oid) {
                            return {
                                ...opp,
                                applications: data.applied
                                    ? [...(opp.applications || []), currentUser._id]
                                    : (opp.applications || []).filter(id => id !== currentUser._id)
                            };
                        }
                        return opp;
                    });
                });
            }
        } catch (error) {
            showToast("Erro", error.message, "error");
        } finally {
            setIsLoading(false);
        }
    };

    // const formattedDate = currentOportunity?.applicationDeadline
    //     ? format(new Date(currentOportunity.applicationDeadline), "dd/MM/yyyy")
    //     : "Data não disponível";

    if (!currentOportunity) return null;

    return (
        // <Flex w="100vw" h="100vh" justifyContent="center">
        //     <Flex width={"50%"} px={5} pb={5} flexDir={"column"} height={"max-content"}>
        //         <Flex flex={1} flexDir={"column"} borderBottom={"1px solid #343434"} >
        //             <Flex justifyContent={"space-between"} flex={1} align={"center"}>
        //                 <Flex flexDir={"row"} gap={4} alignItems={"center"}>
        //                     <Flex>
        //                         <Avatar size={"lg"} />
        //                     </Flex>

        //                     <Flex flexDir={"column"}>
        //                         <Text fontWeight={"bold"} fontSize={"xl"}>{user?.username}</Text>
        //                     </Flex>
        //                 </Flex>

        //                 <Flex>
        //                     <Text color={"#959595"}>
        //                         {formatDistanceToNow(new Date(currentOportunity.createdAt))} ago
        //                     </Text>
        //                 </Flex>
        //             </Flex>

        //             <Flex flexDir={"column"} gap={3} mt={4} mb={3}>
        //                 <Text fontWeight={"bold"}>{currentOportunity.title}</Text>
        //                 <Text>Localização: {currentOportunity.location}</Text>
        //                 <Text>Fim das inscrições: {formattedDate}</Text>
        //                 <Text>{currentOportunity.text}</Text>
        //                 <Box>
        //                     <Text>Inscrições: {currentOportunity.applications?.length || 0} / {currentOportunity.maxApplications}</Text>
        //                 </Box>
        //                 <Button
        //                     variant={"outline"}
        //                     onClick={handleApply}
        //                     isLoading={isLoading}
        //                     loadingText="Processing"
        //                     isDisabled={currentOportunity.applications?.length >= currentOportunity.maxApplications}
        //                 >
        //                     {isApplied ? "Cancelar Inscrição" : "Inscrever-se"}
        //                 </Button>
        //             </Flex>

        //             <Flex>
        //                 <Image
        //                     objectFit={"cover"}
        //                     borderRadius={5}
        //                     src={currentOportunity.img}
        //                 />
        //             </Flex>
        //         </Flex>
        //     </Flex>
        // </Flex>

        <Box bg={bg} minH="100vh">
            <Header />
            <Flex justify="center" p={4} 
            >
                <Box bg={boxBg} w="full" maxW="800px" 
                 borderRadius="lg" shadow="md" //Mudei
                 overflow="hidden"
                 >
                    <Flex p={4} borderBottom="1px" borderColor={colorMode === "dark" ? "#343434" : "gray"}>
                        <Image src={currentOportunity.img} alt={currentOportunity.title} boxSize="64px" mr={4} objectFit="cover" />
                        <VStack align="flex-start" spacing={1}>
                            <Text fontSize="xl" fontWeight="bold">{currentOportunity.title}</Text>
                            <Text color="#03C03C" fontWeight="semibold">{user?.username}</Text>
                            <HStack spacing={4} color={textColor} fontSize="sm">
                                <Flex align="center"><Icon as={FaMapMarkerAlt} mr={1} />{currentOportunity.location}</Flex>
                                <Flex align="center"><Icon as={FaClock} mr={1} />{new Date(currentOportunity.createdAt).toLocaleDateString()}</Flex>
                            </HStack>
                        </VStack>
                    </Flex>

                    <Box p={4}>
                        <HStack spacing={2} mb={4}>
                            <Tag colorScheme="green" size="md">Presencial</Tag>
                        </HStack>

                        <Text fontWeight="semibold" mb={2}>
                            Prazo de candidatura: {new Date(currentOportunity.applicationDeadline).toLocaleDateString() || 'Não informado'}
                        </Text>

                        <Box>
                            <Text>Inscrições: {currentOportunity.applications?.length || 0} / {currentOportunity.maxApplications}</Text>
                        </Box>

                        <Button 
                        color={"#03C03C"}
                        bg={colorMode === "dark" ? "#0A0A0A" : "gray.200"}
                        size="lg" 
                        width="full" 
                        mb={4} 
                        isLoading={isLoading} 
                        loadingText="Processando" 
                        isDisabled={currentOportunity.applications?.length >= currentOportunity.maxApplications}
                        onClick={handleApply}>
                            {isApplied ? "Cancelar Inscrição" : "Inscrever-se"}
                        </Button>

                       

                        <Text fontWeight="bold" mb={2}>Sobre a vaga</Text>
                    <Text color={textColor}>{currentOportunity.text}</Text>
                </Box>
        </Box>
            </Flex >
        </Box >
    )
}

export default OportunityDetailsPage