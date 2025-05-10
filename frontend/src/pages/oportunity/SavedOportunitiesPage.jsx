import { Box, Flex, Spinner, Text, useColorModeValue } from '@chakra-ui/react';
import Oportunities from '../../components/oportunities/Oportunities';
import { useEffect, useState } from 'react';
import useShowToast from '../../hooks/useShowToast';
import { useRecoilValue } from 'recoil';
import userAtom from '../../atoms/userAtom';
import Header from "../../components/header/Header";

const SavedOportunitiesPage = () => {
  const showToast = useShowToast();
  const [oportunities, setOportunities] = useState([]);
  const [loading, setLoading] = useState(true);
  const bg = useColorModeValue('gray.200', '#0A0A0A');
  const boxBg = useColorModeValue('gray.100', '#000000');

  const user = useRecoilValue(userAtom);
  const username = user?.username;

  useEffect(() => {
    const fetchSavedOportunities = async () => {
      if (!username) return; // Impede a execução se username não estiver definido

      setLoading(true);

      try {
        const res = await fetch(`/api/oportunities/saved-oportunities/${username}`);
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

    fetchSavedOportunities();
  }, [showToast, username]); // Inclua username nas dependências

  return (
    <Box>
      <Header/>
    <Box bg={bg} minH="100vh" py={8} px="2cm">
      
      <Flex justifyContent="center">
        <Box w="100%">
          {loading ? (
            <Flex justify="center" alignItems="center" h="300px">
              <Spinner size="xl" />
            </Flex>
          ) : (
            <Box bg={boxBg} p={6} borderRadius="lg" shadow="md">
              <Text fontSize="2xl" fontWeight="bold" mb={2} textAlign="center">
                Oportunidades Salvas
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
                <Text>Nenhuma oportunidade salva encontrada.</Text>
              )}
            </Box>
          )}
        </Box>
      </Flex>
    </Box>
    </Box>
  );
};

export default SavedOportunitiesPage;
