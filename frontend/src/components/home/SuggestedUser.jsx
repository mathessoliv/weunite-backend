/* eslint-disable react/prop-types */
import { Avatar, Box, Button, Card, CardHeader, Flex, Heading, Link, Text, useColorMode } from "@chakra-ui/react"
import useFollowUnfollow from "../../hooks/useFollowUnfollow";
import { Link as RouterLink } from 'react-router-dom'

const SuggestedUser = ({ user }) => {
    // Obtém o modo de cor atual (claro ou escuro)
    const { colorMode } = useColorMode();

    // Hook personalizado para lidar com a lógica de seguir e deixar de seguir usuários
    const { handleFollowUnfollow, following, updating } = useFollowUnfollow(user);

    // Função para limitar o número de caracteres de um texto e adicionar '...' se necessário
    function limitarTexto(texto, maximoCaracteres) {
        if (texto.length > maximoCaracteres) {
            return texto.substring(0, maximoCaracteres) + '...';
        } else {
            return texto;
        }
    }

    return (
        <Box mb={2} w={"100%"}>
            <Card
                variant={"outline"} // Variante do card com borda
                borderRadius="lg"  shadow= {colorMode === "dark" ? "  " : "md"} //Mudei
                bg={colorMode === "dark" ? "#000000" : "gray.100"} // Cor de fundo baseada no modo de cor
                border={colorMode === "dark" ? "1px solid #101010" : ""} // Borda ao redor do box
            >
                <CardHeader>
                    <Flex>
                        <Flex
                            flex={1}
                            justifyContent={"space-between"}
                            alignItems={"center"}
                            gap={3}
                        >
                            {/* Link para o perfil do usuário */}
                            <Link
                                as={RouterLink}
                                to={`/${user.username}`}
                                cursor={"pointer"}
                                style={{ textDecoration: 'none' }}
                            >
                                <Flex alignItems={"center"} gap={4}>
                                    {/* Avatar do usuário */}
                                    <Avatar name={user.username} src={user.profilePic} border={"1px solid #343434"} />
                                    <Box>
                                        {/* Nome e nome de usuário do usuário com limite de caracteres */}
                                        <Heading size={"sm"}>{limitarTexto(user.name, 10)}</Heading>
                                        <Text fontSize={"sm"}>{limitarTexto(user.username, 12)}</Text>
                                    </Box>
                                </Flex>
                            </Link>

                            <Flex alignItems={"center"}>
                                <Box>
                                    {/* Botão para seguir/deixar de seguir o usuário */}
                                    <Button
                                        variant={"outline"}
                                        onClick={handleFollowUnfollow}
                                        isLoading={updating}
                                        size={{ lg: 'sm' }}
                                    >
                                        {following ? "Remover" : "Seguir"}
                                    </Button>
                                </Box>
                            </Flex>
                        </Flex>
                    </Flex>
                </CardHeader>
            </Card>
        </Box>
    )
}

export default SuggestedUser
