import { Box, Button, Flex, HStack, Image, Spinner, Text, useColorModeValue, VStack, useColorMode } from '@chakra-ui/react';
import Oportunities from '../../components/oportunities/Oportunities';
import { useEffect, useState } from 'react';
import useShowToast from '../../hooks/useShowToast';
import { useRecoilState, useRecoilValue } from 'recoil';
import oportunitiesAtom from "../../atoms/postsAtom";
import { useNavigate } from 'react-router-dom';
import Header from '../../components/header/Header';
import CreateOportunity from '../../components/oportunities/CreateOportunity';
import userAtom from '../../atoms/userAtom';

const OportunityPage = () => {
  const showToast = useShowToast();
  const [oportunities, setOportunities] = useRecoilState(oportunitiesAtom);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const bg = useColorModeValue('gray.200', '#0A0A0A');
  const boxBg = useColorModeValue('gray.100', '#000000');
  const boxBg2 = useColorModeValue('gray.100', '#0A0A0A');
  const user = useRecoilValue(userAtom);
  const { colorMode } = useColorMode();

  useEffect(() => {
    const getFeedOportunities = async () => {
      setOportunities([]);
      setLoading(true);

      try {
        const res = await fetch("/api/oportunities/feed");
        const data = await res.json();

        if (data.error) {
          showToast("Erro", data.error, "error");
          return;
        }

        setOportunities(data);
      } catch (error) {
        showToast("Erro", error.message, "error");
      } finally {
        setLoading(false);
      }
    };

    getFeedOportunities();
  }, [showToast, setOportunities]);

  const handleSavedOpportunities = () => {
    const username = user.username; // Supondo que `user` é o estado onde está o usuário autenticado
    navigate(`/saved-oportunities/${username}`);
  };

  const handleMyOpportunities = () => {
    const username = user.username;
    navigate(`/${username}/applied-oportunities`);
  };
  const handleClubOpportunitiesPage = () => {
    const username = user.username;
    navigate(`/club-opportunities/${username}`);  // Roteamento para a página de oportunidades do clube
  };

  return (
    <>
      <Header />
      <Box bg={bg} minH="100vh" py={8} px="2cm"> {/* Adiciona espaço lateral de 2 cm */}
        <Flex justifyContent={"center"}>
          <Box w="100%" px={0}>
            <Flex direction={{ base: 'column', lg: 'row' }} align="flex-start" gap={{ base: 6, lg: 10 }}> {/* Aumenta o espaço entre as colunas */}

              {/* Seção Minhas Oportunidades */}
              <Box
                w={{ base: '100%', lg: '25%' }}
                p={4}
                bg={boxBg}
                borderRadius="lg"
                shadow="md"
                position="sticky"
                top="20px"
                h="fit-content"
                border={colorMode === "dark" ? "1px solid #101010" : ""}
              >
                <Text fontSize="lg" fontWeight="bold" mb={4}>Minhas Oportunidades</Text>

                <Box mb={4} p={4} bg={boxBg2} border={colorMode === "dark" ? "1px solid #101010" : ""} borderRadius="md" shadow="md" onClick={handleSavedOpportunities} cursor="pointer">
                  <HStack>
                    <VStack align="start">
                      <Text fontWeight="bold">Oportunidades Salvas</Text>
                      <Text fontSize="sm" color="gray">Veja as oportunidades que você salvou.</Text>
                    </VStack>
                  </HStack>
                </Box>

                <Box p={4} bg={boxBg2} border={colorMode === "dark" ? "1px solid #101010" : ""} borderRadius="md" shadow="md" onClick={handleMyOpportunities} cursor="pointer">
                  <HStack>
                    <VStack align="start">
                      <Text fontWeight="bold">Minhas Oportunidades</Text>
                      <Text fontSize="sm" color="gray">Acesse as oportunidades que você se candidatou.</Text>
                    </VStack>
                  </HStack>
                </Box>

                

                <br />
                {user && user.isClub === true && (
                  <Box p={4} bg={boxBg2} border={colorMode === "dark" ? "1px solid #101010" : ""} borderRadius="md" shadow="md" onClick={handleClubOpportunitiesPage} cursor="pointer">
                    <HStack>
                      <VStack align="start">
                        <Text fontWeight="bold">Oportunidades criadas</Text>
                        <Text fontSize="sm" color="gray">Acesse as oportunidades que você criou.</Text>
                      </VStack>
                    </HStack>
                  </Box>
                )}
                {/* Condicional para exibir o botão "Minhas Oportunidades" para clubes */}
              </Box>

              {/* Seção Oportunidades Selecionadas */}
              <Box w={{ base: '100%', lg: '75%' }}>
                {loading ? (
                  <Flex justify="center" alignItems="center" h="300px">
                    <Spinner size="xl" />
                  </Flex>
                ) : (
                  <Box bg={boxBg} p={6} border={colorMode === "dark" ? "1px solid #101010" : ""} borderRadius="lg" shadow="md">
                    <Text fontSize="2xl" fontWeight="bold" mb={2} textAlign="initial">
                      Oportunidades selecionadas para você
                    </Text>
                    <Text fontSize="lg" color={colorMode === "dark" ? "white" : "black"} mb={6} textAlign="initial">
                      Com base no seu perfil e histórico de pesquisas
                    </Text>

                    {oportunities.length > 0 ? (
                      oportunities.map((oportunity) => (
                        <Oportunities
                          key={oportunity._id}
                          oportunity={oportunity}
                          postedBy={oportunity.postedBy}
                        />
                      ))
                    ) : (
                      <Text>Nenhuma oportunidade encontrada.</Text>
                    )}
                  </Box>
                )}
              </Box>
            </Flex>
          </Box>
        </Flex>
        {user.isClub === true && (
          <CreateOportunity />
        )}
      </Box>
    </>
  );
};

export default OportunityPage;
