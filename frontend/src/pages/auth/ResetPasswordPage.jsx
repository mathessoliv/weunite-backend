import { Button, Flex, FormControl, Heading, Input, Text,useColorMode } from "@chakra-ui/react"
import { useAuthStore } from "../../store/authStore";
import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useShowToast from "../../hooks/useShowToast";
import { PasswordStrengthMeter } from "../../components/auth/PasswordStrengthMeter";

const ResetPasswordPage = () => {
    const inputStyles = {
        '&:-webkit-autofill': {
            WebkitBoxShadow: '0 0 0 1000px #101010 inset',
            WebkitTextFillColor: '#ffffff',
            borderColor: '#03C03C'
        },
        '&:-webkit-autofill:focus': {
            WebkitBoxShadow: '0 0 0 1000px #000000 inset',
        },
        '&:-webkit-autofill:hover': {
            borderColor: '#505050',
            transition: '0.5s'
        }
    };

    const { colorMode } = useColorMode();//Mudei
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const { resetPassword, isLoading } = useAuthStore();
    const showToast = useShowToast();

    const { token } = useParams();
    const navigate = useNavigate();

    const validatePassword = (pass) => {
        if (pass.length < 6) {
            return "A senha deve ter pelo menos 6 caracteres";
        }
        if (!/[A-Z]/.test(pass)) {
            return "A senha deve conter pelo menos uma letra maiúscula";
        }
        if (!/[a-z]/.test(pass)) {
            return "A senha deve conter pelo menos uma letra minúscula";
        }
        if (!/\d/.test(pass)) {
            return "A senha deve conter pelo menos um número";
        }
        if (!/[^A-Za-z0-9]/.test(pass)) {
            return "A senha deve conter pelo menos um caractere especial";
        }
        return null; // Senha válida
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            showToast("Erro", "As senhas não são iguais", "error");
            return;
        }

        const passwordError = validatePassword(password);
        if (passwordError) {
            showToast("Erro", passwordError, "error");
            return;
        }

        try {
            await resetPassword(token, password);

            showToast("Sucesso", "Senha resetada com sucesso!", "success")
            setTimeout(() => {
                navigate("/auth");
            }, 1000);
        } catch (error) {
            console.error(error);
            showToast("Erro", "Erro ao reiniciar a senha", "error")
        }
    };

    const isPasswordValid = (pass) => {
        return validatePassword(pass) === null;
    };

    return (
        <Flex width={"100vw"} height={"100vh"} alignItems={"center"} justifyContent={"center"}
        backgroundColor={colorMode === "dark" ? "#0A0A0A" : "gray.200"} //Mudei
        >
            <Flex flexDir={"column"} backgroundColor={colorMode === "dark" ? "black" : "gray.100"} //Mudei
                borderRadius="lg" shadow="md" //Mudei
                border={colorMode === "dark" ? "1px solid #101010" : ""} //Mudei
                  p={10}>
                <Flex alignItems={"center"} justifyContent={"center"} flexDir={"column"} p={3} gap={3}>
                    <Heading>Reiniciar senha</Heading>
                    <Text color={'#03C03C'}>Insira sua nova senha</Text>
                </Flex>

                <Flex p={5} gap={3}>
                    <FormControl as={"form"} onSubmit={handleSubmit} >
                        <Flex flexDirection="column">
                            
                            <Input
                                borderColor={"#343434"} //Mudei
                                type='text'
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                width={"100%"}
                                height={12}
                                mb={3}
                               
                                color={colorMode === "dark" ? "#FFFFFF" : "#000000"} //Mudei
                                focusBorderColor="#03C03C"
                                placeholder="Nova senha"
                            />
                            
                        </Flex>

                        <Flex mt={4}>
                            <Input
                                borderColor={"#343434"} //Mudei
                                type='text'
                                placeholder='Confirme sua nova senha'
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                                width={"100%"}
                                height={12}
                                
                                color={colorMode === "dark" ? "#FFFFFF" : "#000000"} //Mudei
                                focusBorderColor="#03C03C"
                            />
                        </Flex>

                        <PasswordStrengthMeter password={password} />

                        <Flex justifyContent="center" width="100%" mt={3}>
                            <Button 
                                type='submit' 
                                width={"100%"} 
                                variant={"outline"} 
                                disabled={isLoading || !isPasswordValid(password)}
                            >
                                {isLoading ? "Trocando..." : "Trocar Senha"}
                            </Button>
                        </Flex>
                    </FormControl>
                </Flex>
            </Flex>
        </Flex>
    )
}

export default ResetPasswordPage