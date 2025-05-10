import { Button, Flex, FormControl, FormLabel, Heading, Input, RequiredIndicator, Text, useColorMode} from "@chakra-ui/react"
import { useState } from "react";
import useShowToast from "../../hooks/useShowToast";
import { useNavigate } from "react-router-dom";

const ForgotPasswordPage = () => {
    const [email, setEmail] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const showToast = useShowToast();
    const navigate = useNavigate();

    const { colorMode } = useColorMode();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const res = await fetch("/api/auth/forgot-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email })
            });

            const data = await res.json();

            if (!res.ok) {
                showToast("Erro", data.message || "Um erro ocorreu", "error");
                return;
            }

            showToast("Sucesso", data.message, "success");
            setEmail("");
            navigate(`/verify-reset-code`);
        } catch (error) {
            showToast("Erro", error.message, "error");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Flex width={"100vw"} height={"100vh"} alignItems={"center"} justifyContent={"center"}
        backgroundColor={colorMode === "dark" ? "#0A0A0A" : "gray.200"}
        >
            <Flex flexDir={"column"} border={colorMode === "dark" ? "1px solid #101010" : ""}  borderRadius="lg" shadow="md"
             p={10} justifyContent={"center"}
             backgroundColor={colorMode === "dark" ? "black" : "gray.100"}
             >

                <Flex alignItems={"center"} justifyContent={"center"} flexDir={"column"} gap={2}>
                    <Heading>Esqueceu senha</Heading>
                    <Text color={'#03C03C'}>Insira seu e-mail</Text>
                </Flex>
                <Flex flexDir={"column"}>
                    <FormControl as="form" onSubmit={handleSubmit} isRequired p={3}>
                        <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Email</FormLabel>
                        <Input
                            borderColor={"#343434"}
                            type='email'
                            placeholder='Email'
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            focusBorderColor="#03C03C"
                        />
                        <Flex justifyContent="center" width="100%" mt={3}>
                            <Button 
                                type="submit" 
                                variant={"outline"} 
                                width={"100%"}
                                isLoading={isLoading}
                                loadingText="Enviando"
                            >
                                Enviar
                            </Button>
                        </Flex>
                    </FormControl>
                </Flex>
            </Flex>
        </Flex>
    )
}

export default ForgotPasswordPage