/* eslint-disable react/prop-types */
import { useState } from 'react';
import {
    Flex,
    Box,
    FormControl,
    FormLabel,
    Input,
    InputGroup,
    HStack,
    InputRightElement,
    Stack,
    Button,
    Text,
    useColorModeValue,
    Link,
    Spinner,
    RequiredIndicator,
    Image,
    useColorMode
} from '@chakra-ui/react';
import { ViewIcon, ViewOffIcon } from '@chakra-ui/icons';
import { useSetRecoilState } from 'recoil';
import { Check, X } from "lucide-react";
import userAtom from '../../../atoms/userAtom';
import useShowToast from '../../../hooks/useShowToast';

// Importe seus átomos e hooks aqui
import authScreenAtom from '../../../atoms/authAtom';
import TermsCheckbox from '../TermsCheckBox';


const PasswordCriteria = ({ password }) => {
    const criteria = [
        { label: "Pelo menos 6 caracteres", met: password.length >= 6 },
        { label: "Contém letra maiuscula", met: /[A-Z]/.test(password) },
        { label: "Contém letra minuscula", met: /[a-z]/.test(password) },
        { label: "Contém um número", met: /\d/.test(password) },
        { label: "Contém um caráctere especial(ex:!@#$%^&*)", met: /[^A-Za-z0-9]/.test(password) },
    ];

    return (
        <Stack spacing={1} mt={2}>
            {criteria.map((item) => (
                <HStack key={item.label} spacing={2}>
                    {item.met ? (
                        <Check size={16} color="green" />
                    ) : (
                        <X size={16} color="#959595" />
                    )}
                    <Text fontSize="xs" color={item.met ? "green.500" : "#959595"}>{item.label}</Text>
                </HStack>
            ))}
        </Stack>
    );
};

const PasswordStrengthMeter = ({ password }) => {
    const getStrength = (pass) => {
        let strength = 0;
        if (pass.length >= 6) strength++;
        if (pass.match(/[a-z]/) && pass.match(/[A-Z]/)) strength++;
        if (pass.match(/\d/)) strength++;
        if (pass.match(/[^a-zA-Z\d]/)) strength++;
        return strength;
    };

    const strength = getStrength(password);

    const getColor = (strength) => {
        if (strength <= 1) return "red.500";
        if (strength === 2) return "yellow.500";
        if (strength === 3) return "yellow.400";
        return "green.500";
    };

    const getStrengthText = (strength) => {
        if (strength === 0) return "Muito fraca";
        if (strength === 1) return "Fraca";
        if (strength === 2) return "Média";
        if (strength === 3) return "Boa";
        return "Forte";
    };

    return (
        <Box mt={2}>
            <Flex justify="space-between" align="center" mb={1}>
                <Text fontSize="xs" color="#959595">Força da senha</Text>
                <Text fontSize="xs" color="#959595">{getStrengthText(strength)}</Text>
            </Flex>
            <Flex>
                {[...Array(4)].map((_, index) => (
                    <Box
                        key={index}
                        height="2px"
                        width="25%"
                        bg={index < strength ? getColor(strength) : "#959595"}
                        mr={index < 3 ? 1 : 0}
                    />
                ))}
            </Flex>
            <PasswordCriteria password={password} />
        </Box>
    );
};

export default function IntegratedSignUpCard() {

    const [termsAccepted, setTermsAccepted] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

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

    const [showPassword, setShowPassword] = useState(false);
    const [inputs, setInputs] = useState({
        name: "",
        username: "",
        email: "",
        password: "",
    });

    const setAuthScreen = useSetRecoilState(authScreenAtom);
    const setUser = useSetRecoilState(userAtom);
    const toast = useShowToast();

    const validatePassword = (password) => {
        const criteria = [
            { test: password.length >= 6, message: "A senha deve ter pelo menos 6 caracteres" },
            { test: /[A-Z]/.test(password), message: "A senha deve conter pelo menos uma letra maiúscula" },
            { test: /[a-z]/.test(password), message: "A senha deve conter pelo menos uma letra minúscula" },
            { test: /\d/.test(password), message: "A senha deve conter pelo menos um número" },
            { test: /[^A-Za-z0-9]/.test(password), message: "A senha deve conter pelo menos um caractere especial" },
        ];

        for (let criterion of criteria) {
            if (!criterion.test) {
                return criterion.message;
            }
        }

        return null;
    };

    const handleSignup = async () => {
        if (!inputs.name || !inputs.username || !inputs.email || !inputs.password) {
            toast("Error", "Todos campos são obrigatórios!", 'error');
            return;
        }

        const passwordError = validatePassword(inputs.password);
        if (passwordError) {
            toast("Error", passwordError, "error");
            return;
        }

        if (!termsAccepted) {
            toast("Error", "Você precisa aceitar os termos e políticas para continuar", 'error');
            return;
        }

        setIsLoading(true);

        try {
            const res = await fetch("/api/auth/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(inputs)
            });
            
            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.message || "Erro no cadastro");
            }

            if (data.error) {
                toast("Error", data.error, 'error');
                return;
            }

            setUser(data.user);
            window.location.href = '/verify-email';

        } catch (error) {
            toast("Erro", error.message, 'error');
        } finally {
            setIsLoading(false);
        }
    };
    const { colorMode } = useColorMode();

    return (
        <Flex
            minH={'100vh'}
            align={'center'}
            justify={'center'}
            bg={useColorModeValue('gray.200', '#0A0A0A')} //Mudei
            overflowY={"hidden"}>

            <Stack spacing={4} mx={'auto'} maxW={'lg'} py={8} px={6}>
                <Stack align={'center'} spacing={0} >
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
                        para aproveitar nossas funcionalidades
                    </Text>
                </Stack>

                <Stack mx={'auto'} maxW={'lg'} py={0} px={6} >

                    <Box
                        rounded={'lg'}
                        bg={useColorModeValue('gray.100', '#000000')} //Mudei
                        boxShadow={'lg'}
                        px={8}
                        py={6}
                        border={useColorModeValue("", "1px solid #101010")} //Mudei
                        overflowY={"hidden"}>
                        <Stack spacing={4}>
                            <HStack>
                                <Box>
                                    <FormControl isRequired>
                                        <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Nome completo</FormLabel>
                                        <Input
                                            placeholder='Nome completo'
                                            type="text"
                                            borderColor={"#343434"} //Mudei
                                            onChange={(e) => setInputs({ ...inputs, name: e.target.value })}
                                            value={inputs.name}
                                            focusBorderColor="#03C03C"
                                        />
                                    </FormControl>
                                </Box>
                                <Box>
                                    <FormControl isRequired>
                                        <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Nome de usuário</FormLabel>
                                        <Input
                                            placeholder='Nome de usuário' 
                                            type="text"
                                            borderColor={"#343434"} //Mudei
                                            onChange={(e) => setInputs({ ...inputs, username: e.target.value })}
                                            value={inputs.username}
                                            focusBorderColor="#03C03C"
                                        />
                                    </FormControl>
                                </Box>
                            </HStack>
                            <FormControl isRequired>
                                <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Endereço de email</FormLabel>
                                <Input
                                    placeholder='E-mail' 
                                    type="email"
                                    borderColor={"#343434"} //Mudei
                                    onChange={(e) => setInputs({ ...inputs, email: e.target.value })}
                                    value={inputs.email}
                                    focusBorderColor="#03C03C"
                                    sx={inputStyles}
                                />
                            </FormControl>
                            <FormControl isRequired>
                                <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Senha</FormLabel>
                                <InputGroup>
                                    <Input
                                        placeholder='Senha' 
                                        type={showPassword ? 'text' : 'password'}
                                        borderColor={"#343434"} //Mudei
                                        onChange={(e) => setInputs({ ...inputs, password: e.target.value })}
                                        value={inputs.password}
                                        focusBorderColor="#03C03C"
                                        minLength={8}
                                        sx={inputStyles}
                                    />
                                    <InputRightElement h={'full'}>
                                        <Button
                                            variant={'ghost'}
                                            onClick={() => setShowPassword((showPassword) => !showPassword)}>
                                            {showPassword ? <ViewIcon /> : <ViewOffIcon />}
                                        </Button>
                                    </InputRightElement>
                                </InputGroup>
                            </FormControl>
                            <PasswordStrengthMeter password={inputs.password} />
                            <TermsCheckbox isChecked={termsAccepted} onTermsAccepted={setTermsAccepted} />
                            <Stack>
                                <Button
                                    loadingText="Enviando"
                                    size="lg"
                                    variant={"outline"}
                                    onClick={handleSignup}
                                    isDisabled={!termsAccepted}
                                >
                                    {isLoading ? <Spinner size="sm" color="#03C03C" /> : "Cadastrar"}
                                </Button>
                            </Stack>
                            <Stack>
                                <Text align={'center'} fontSize={'sm'} color={"#959595"}>
                                    Já é um usuário? <Link color={'#03C03C'} onClick={() => setAuthScreen("login")} _hover={{
                                        textDecoration: 'none'
                                    }}>Login</Link>
                                </Text>
                                <Text align={'center'} fontSize={'sm'} color={"#959595"}>
                                    É uma empresa? <Link color={'#03C03C'} onClick={() => setAuthScreen("companysignup")} _hover={{
                                        textDecoration: 'none'
                                    }}>Clique aqui</Link>
                                </Text>

                            </Stack>
                        </Stack>
                    </Box>

                </Stack>
                </Stack>
        </Flex>
    );
}