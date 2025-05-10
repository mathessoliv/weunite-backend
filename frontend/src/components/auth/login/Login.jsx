import {
    Flex,
    Box,
    FormControl,
    FormLabel,
    Input,
    InputGroup,
    InputRightElement,
    Stack,
    Button,
    Heading,
    Text,
    useColorModeValue,
    Link,
    RequiredIndicator,
    Image,
    useColorMode
} from '@chakra-ui/react'
import { useEffect, useState } from 'react'
import { ViewIcon, ViewOffIcon } from '@chakra-ui/icons'
import { useSetRecoilState } from 'recoil'
import authScreenAtom from '../../../atoms/authAtom'
import userAtom from '../../../atoms/userAtom'
import useShowToast from '../../../hooks/useShowToast'
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { GoogleLogin } from '@react-oauth/google'

export default function Login() {
    const inputStyles = {
        '&:-webkit-autofill': {
            WebkitBoxShadow: `0 0 0 1000px ${useColorModeValue('gray.200', '#101010')} inset`, //MUDEI
            WebkitTextFillColor: useColorModeValue('#000000', '#ffffff'), //MUDEI
            borderColor: useColorModeValue('#03C03C', '#03C03C'), //MUDEI
        },
        '&:-webkit-autofill:focus': {
            WebkitBoxShadow: `0 0 0 1000px ${useColorModeValue('gray.200', '#000000')} inset`, //Mudei
        },
        '&:-webkit-autofill:hover': {
            borderColor: '#505050',
            transition: '0.5s'
        },
    };
    // Estado para controlar a visibilidade da senha
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    const { colorMode} = useColorMode();
    // Hook para mostrar notificações
    const toast = useShowToast();

    // Funções para atualizar o estado global com o Recoil
    const setAuthScreen = useSetRecoilState(authScreenAtom);
    const setUser = useSetRecoilState(userAtom);

    // Estado para armazenar os valores dos inputs
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
    });

    // Estado para controlar o carregamento durante o login
    const [loading, setLoading] = useState(false)

    const handleGoogleLoginSuccess = async (credentialResponse) => {
        try {
            const res = await fetch('/api/auth/google-login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    token: credentialResponse.credential, // O token de autenticação retornado pelo Google
                }),
            });

            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.message || 'Falha ao fazer login com Google');
            }

            localStorage.setItem('user-threads', JSON.stringify(data));
            setUser(data);  // Atualiza o estado com o usuário autenticado
            navigate('/');  // Redireciona para a página principal após o login
        } catch (error) {
            toast({
                title: 'Erro no login com Google',
                description: error.message,
                status: 'error',
                duration: 5000,
                isClosable: true,
            });
        }
    };

    // Função para lidar com o login
    const handleLogin = async () => {
        if (!inputs.username || !inputs.password) {
            toast({
                title: "Campos obrigatórios",
                description: "Por favor, preencha todos os campos.",
                status: "warning",
                duration: 3000,
                isClosable: true,
            });
            return;
        }

        setLoading(true);
        try {
            const res = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(inputs),
            });

            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.message || "An error occurred during login");
            }

            if (!data._id) {
                throw new Error("Login failed. User data is incomplete.");
            }

            localStorage.setItem("user-threads", JSON.stringify(data));
            setUser(data);
            navigate('/'); // Redireciona para a home apenas em caso de sucesso
        } catch (error) {
            toast('Error', error.toString(), 'error');
            // Limpa o localStorage e o estado do usuário em caso de erro
            localStorage.removeItem("user-threads");
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const handleEnterPress = (event) => {
            if (event.key === 'Enter') {
                handleLogin();
            }
        };

        window.addEventListener('keydown', handleEnterPress);

        // Cleanup do event listener
        return () => {
            window.removeEventListener('keydown', handleEnterPress);
        };
    }, [inputs]);

    return (
        <Flex
            minH={'100vh'} // Altura mínima da tela
            align={'center'} // Alinha verticalmente ao centro
            justify={'center'} // Alinha horizontalmente ao centro
            bg={useColorModeValue('gray.200', '#0A0A0A')} // Cor de fundo baseada no modo de cor
        >
            <Stack spacing={4} mx={'auto'} maxW={'lg'} py={8} px={6}>
                <Stack align={'center'}  spacing={0} >
                        <Image
                            src={colorMode === "dark" ? "/public/1.png" : "/public/2.png"}
                            alt='Logo'
                            width="200px"  // Define a largura da imagem
                            height="auto"  // Mantém a proporção original da imagem
                            fontSize={{ base: "25px", md: "30px", lg: "35px" }}
                            maxW={{ base: "100px", md: "150px", lg: "200px" }}
                            mt={4}
                        />
                    <Text fontSize={'lg'} color={'#03C03C'}>
                        Seja bem vindo de volta!
                    </Text>
                </Stack>
                <Box
                    rounded={'lg'}
                    bg={useColorModeValue('gray.100', '#000000')} // Cor de fundo baseada no modo de cor
                    borderRadius="lg" shadow="md" //Mudei
                    p={12}
                    border={useColorModeValue("", "1px solid #101010")} // Borda ao redor do box
                    width={430}
                    height={430}
                >
                    <Stack spacing={4}>
                        <FormControl id="email" isRequired>
                            <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />} fontWeight={"bold"}>Username</FormLabel>
                            <Input
                                placeholder='Digite seu nome de usuário'
                                type="text"
                                borderColor={"#343434"} //Mudei
                                value={inputs.username}
                                onChange={(e) => setInputs((inputs) => ({ ...inputs, username: e.target.value }))}
                                sx={inputStyles}
                                _focus={{
                                    borderColor: "#03C03C",  // Cor verde quando o input está em foco
                                    boxShadow: '0 0 0 1px #03C03C'  // Adiciona um pequeno sombreado verde ao redor do input
                                }}
                            />
                        </FormControl>
                        <FormControl id="password" isRequired>
                            <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />} fontWeight={"bold"}>Senha</FormLabel>
                            <InputGroup>
                                <Input
                                    placeholder='Digite sua senha'
                                    type={showPassword ? 'text' : 'password'}
                                    borderColor={"#343434"} //Mudei
                                    value={inputs.password}
                                    onChange={(e) => setInputs((inputs) => ({ ...inputs, password: e.target.value }))}
                                    sx={inputStyles}
                                    _focus={{
                                        borderColor: "#03C03C",  // Cor verde quando o input está em foco
                                        boxShadow: '0 0 0 1px #03C03C'  // Adiciona um pequeno sombreado verde ao redor do input
                                    }}
                                />
                                <InputRightElement h={'full'}>
                                    <Button
                                        variant={'ghost'}
                                        onClick={() => setShowPassword((showPassword) => !showPassword)}
                                    >
                                        {showPassword ? <ViewIcon /> : <ViewOffIcon />}
                                    </Button>
                                </InputRightElement>
                            </InputGroup>
                        </FormControl>
                        <Stack spacing={10}>
                            <Button
                                loadingText="Entrando"
                                size="lg"
                                variant={"outline"}
                                onClick={handleLogin}
                                isLoading={loading}
                            >
                                Entrar
                            </Button>
                        </Stack>
                        <Stack alignItems={"center"} justifyContent={"center"}>

                            <GoogleLogin
                                onSuccess={handleGoogleLoginSuccess}
                                onError={() => {
                                    toast({
                                        title: 'Falha ao fazer login com Google',
                                        description: 'Não foi possível completar o login com Google.',
                                        status: 'error',
                                        duration: 5000,
                                        isClosable: true,
                                    });
                                }}
                            />

                            <Text align={'center'} fontSize={"sm"} color={"#959595"}>
                                Não tem uma conta? <Link color={'#03C03C'} onClick={() => setAuthScreen("signup")} _hover={{
                                    textDecoration: 'none'
                                }}>Cadastre-se</Link>
                            </Text>

                            <Text align={"center"} fontSize={"sm"} color={"#959595"}>
                                Esqueceu a senha?  <Link color={'#03C03C'} as={RouterLink} to={`/forgot-password`} _hover={{
                                    textDecoration: 'none'
                                }}> Clique aqui</Link>
                            </Text>

                            <Text align={"center"} fontSize={'sm'}>
                                <Link color={'#03C03C'} as={RouterLink} to={`/verify-email`} _hover={{
                                    textDecoration: 'none'
                                }}>Verifique seu email</Link>
                            </Text>
                        </Stack>
                    </Stack>
                </Box>
            </Stack>
        </Flex>
    )
}
