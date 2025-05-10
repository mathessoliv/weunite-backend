/* eslint-disable react/prop-types */
import { Box, Button, Divider, Flex, HStack, Image, Text, useColorModeValue,useColorMode } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import useShowToast from "../../hooks/useShowToast";
import { useNavigate } from "react-router-dom";
import { useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";

const Oportunities = ({ oportunity, postedBy }) => {
    const [user, setUser] = useState(null);
    const showToast = useShowToast();
    const navigate = useNavigate();
    const [isSaved, setIsSaved] = useState(false); // Estado para saber se está salvo
    const [updating, setUpdating] = useState(false);
    const currentUser = useRecoilValue(userAtom); // Usuário atual
    const boxBg = useColorModeValue('gray.100', '#0A0A0A');
    const textColor = useColorModeValue('gray.600', 'gray');
    const dividerColor = useColorModeValue('gray.800', 'white');
    const { colorMode } = useColorMode();

    useEffect(() => {
        if (currentUser && oportunity) {
            // Faz a requisição para verificar se a oportunidade está salva
            const checkIfSaved = async () => {
                try {
                    const res = await fetch(`/api/oportunities/saved/${oportunity._id}`, {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
                    const data = await res.json();
                    if (data.error) {
                        showToast("Erro", data.error, "error");
                        return;
                    }

                    // Atualiza o estado 'isSaved' com base na resposta da API
                    setIsSaved(data.isSaved);
                } catch (error) {
                    showToast("Erro", error.message, "error");
                }
            };

            checkIfSaved();
        }
    }, [currentUser, oportunity, showToast]);

    const handleSaveUnsave = async () => {
        if (!currentUser) {
            showToast("Erro", "Por favor, faça login para salvar oportunidades", "error");
            return;
        }
        if (updating) return;

        setUpdating(true);
        try {
            const res = await fetch(`/api/oportunities/save/${oportunity._id}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            const data = await res.json();
            if (data.error) {
                showToast("Erro", data.error, "error");
                return;
            }

            setIsSaved(!isSaved); // Alterna o estado de salvo
            showToast("Sucesso", data.message, "success");

        } catch (error) {
            showToast("Erro", error.message, "error");
        } finally {
            setUpdating(false);
        }
    };

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

    if (!user) return null;

    const handleOpportunityClick = () => {
        navigate(`/${user.username}/oportunities/${oportunity._id}`);
    };

    return (
        <>
            <Box
                cursor="pointer"
                p={3}
                bg={boxBg}
                borderRadius="md"
                shadow={useColorModeValue === "dark" ? "" : "lg"}
                mb={4}
                border={colorMode === "dark" ? "1px solid #101010" : ""} // Borda ao redor do box
                
            >
                <Flex direction={{ base: 'column', md: 'row' }} justify="space-between" align="start">
                    <HStack spacing={4} mb={{ base: 4, md: 0 }} onClick={handleOpportunityClick}>
                        <Image boxSize="70px" src={oportunity.img || '/images/default.png'} alt={oportunity.title} borderRadius="md" />
                        <Box>
                            <Text fontSize="lg" fontWeight="bold" mb={1}>{oportunity.title}</Text>
                            <Text fontSize="md" color={textColor} mb={1}>
                                {user?.username} - {oportunity.location || 'Local não informado'}
                            </Text>
                            <Text fontSize="sm" color="gray" mb={1}>{oportunity.text}</Text>
                            <Text fontSize="sm" color="gray" mb={1}>
                                Prazo de Candidatura: {new Date(oportunity.applicationDeadline).toLocaleDateString() || 'Não especificado'}
                            </Text>
                            <Text fontSize="sm" color="gray">
                                Data de Criação: {new Date(oportunity.createdAt).toLocaleDateString() || 'Não especificado'}
                            </Text>
                        </Box>
                    </HStack>
                </Flex>
                <Divider my={4} borderColor={dividerColor} />
                <Button
                    onClick={(e) => {
                        e.stopPropagation();
                        handleSaveUnsave();
                    }}
                    isLoading={updating}
                    bg={colorMode === "dark" ? "#000000" : "gray.200"}
                >
                    {isSaved ? "Remover dos salvos" : "Salvar oportunidade"}
                </Button>
            </Box>
        </>
    );
};

export default Oportunities;

