/* eslint-disable react/prop-types */
import { Box, Text, HStack, Image, IconButton } from "@chakra-ui/react";
import { CloseIcon } from "@chakra-ui/icons";
import { useNavigate } from 'react-router-dom'; // Para navegação ao clicar na vaga

const PostVaga = ({ vaga }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    // Navegação para a página de detalhes da vaga ou realizar alguma ação
    navigate(`/vagas/${vaga.id}`);
  };

  return (
    <Box
      bg="white"
      p={4}
      borderRadius="md"
      shadow="sm"
      w="100%"
      cursor="pointer"
      _hover={{ bg: "gray.100" }} // Efeito hover para indicar clique
      onClick={handleClick}
    >
      <HStack justify="space-between" w="100%">
        <HStack>
          <Image boxSize="50px" src={vaga.imageUrl} alt={vaga.company} borderRadius="md" />
          <Box>
            <Text fontWeight="bold">{vaga.title}</Text>
            <Text color="gray.600">{vaga.company} - {vaga.location}</Text>
            <Text fontSize="sm" color="gray.500">
              {vaga.description}
            </Text>
            <Text color="gray.500">Visualizado</Text>
          </Box>
        </HStack>
        <IconButton icon={<CloseIcon />} aria-label="Fechar" />
      </HStack>
    </Box>
  );
};

export default PostVaga;
