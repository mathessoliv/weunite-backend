/* eslint-disable react/prop-types */
import { Box, Button, Card, CardBody, CardFooter, CardHeader, Heading, Text, useColorMode,} from '@chakra-ui/react'
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useShowToast from '../../hooks/useShowToast';

const SuggestedOportunity = ({ title, text, _id, postedBy }) => {
    const { colorMode } = useColorMode();
    const navigate = useNavigate();
    const showToast = useShowToast();
    const [user, setUser] = useState(null);

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

    function limitarTexto(texto, maximoCaracteres) {
        if (texto.length > maximoCaracteres) {
            return texto.substring(0, maximoCaracteres) + '...';
        } else {
            return texto;
        }
    }

    const handleClick = () => {
        if (user && _id) {
            navigate(`/${user.username}/oportunities/${_id}`);
        } else {
            showToast("Erro", "Dados da oportunidade incompletos", "error");
        }
    };

    return (
        <Box>
            <Card
                
               variant="outline"
               borderRadius="lg"
               shadow= {colorMode === "dark" ? "" : "md"} //Mudei
               bg={colorMode === "dark" ? "#000000" : "gray.100"}
               border={colorMode === "dark" ? "1px solid #101010" : ""} // Borda ao redor do box
               m={2}
               minH={250}
            >
                <CardHeader>
                    <Heading size="md">
                        {limitarTexto(title, 20)}
                    </Heading>
                </CardHeader>
                <CardBody>
                    <Text size="sm">
                        {limitarTexto(text, 80)}
                    </Text>
                </CardBody>
                <CardFooter>
                    <Button
                        onClick={handleClick}
                        variant="outline"
                        isDisabled={!user || !_id}
                    >
                        Veja aqui
                    </Button>
                </CardFooter>
            </Card>
        </Box>
    )
}

export default SuggestedOportunity
