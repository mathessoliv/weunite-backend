/* eslint-disable react/prop-types */
import { useState } from 'react';
import {
    Flex,
    Box,
    FormControl,
    FormLabel,
    Input,
    HStack,
    Stack,
    Button,
    Heading,
    Text,
    useColorModeValue,
    Link,
    Spinner,
    FormHelperText,
    RequiredIndicator,
} from '@chakra-ui/react';
import { useSetRecoilState } from 'recoil';
import useShowToast from '../../../hooks/useShowToast';

// Importe seus átomos e hooks aqui
import authScreenAtom from '../../../atoms/authAtom';
import TermsCheckbox from '../TermsCheckBox';

const validateCNPJ = (cnpj) => {
    cnpj = cnpj.replace(/\D/g, ''); // Remove caracteres não numéricos

    if (cnpj.length !== 14) return false;

    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1+$/.test(cnpj)) return false;

    let tamanho = cnpj.length - 2;
    let numeros = cnpj.substring(0, tamanho);
    let digitos = cnpj.substring(tamanho);
    let soma = 0;
    let pos = tamanho - 7;

    // Calcula o primeiro dígito verificador
    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }
    let resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
    if (resultado !== parseInt(digitos.charAt(0), 10)) return false;

    // Calcula o segundo dígito verificador
    tamanho = tamanho + 1;
    numeros = cnpj.substring(0, tamanho);
    soma = 0;
    pos = tamanho - 7;

    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }
    resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
    if (resultado !== parseInt(digitos.charAt(1), 10)) return false;

    return true;
};

export default function CompanySignUpCard() {

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
    const [isCNPJValid, setIsCNPJValid] = useState(true);
    const [inputs, setInputs] = useState({
        name: "",
        username: "",
        email: "",
        cnpj: "",
    });

    const setAuthScreen = useSetRecoilState(authScreenAtom);
    const toast = useShowToast();

    const handleCNPJChange = (e) => {
        const rawValue = e.target.value.replace(/\D/g, '');
        if (rawValue.length <= 14) {
            const formattedCNPJ = rawValue
                .replace(/^(\d{2})(\d)/, '$1.$2')
                .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
                .replace(/\.(\d{3})(\d)/, '.$1/$2')
                .replace(/(\d{4})(\d)/, '$1-$2');
            setInputs({ ...inputs, cnpj: formattedCNPJ });
            setIsCNPJValid(validateCNPJ(rawValue));
        }
    };


    const handleSignup = async () => {
        if (!isCNPJValid) {
            toast("Erro", "CNPJ inválido", "error");
            return;
        }
        if (!inputs.name || !inputs.username || !inputs.email || !inputs.cnpj) {
            toast("Error", "Todos campos são obrigatórios!", 'error');
            return;
        }

        if (!termsAccepted) {
            toast("Error", "Você precisa aceitar os termos e políticas para continuar", 'error');
            return;
        }

        setIsLoading(true);

        try {
            const res = await fetch("/api/auth/signupcompanysolicitation", {
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

            setInputs({
                name: "",
                username: "",
                email: "",
                cnpj: ""
            });

            toast("Sucesso", "Solicitação enviada com sucesso! Verifique seu email para validar seu cadastro", 'success');

        } catch (error) {
            toast("Erro", error.message, 'error');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Flex
            minH={'100vh'}
            align={'center'}
            justify={'center'}
            bg={useColorModeValue('gray.200', '#0A0A0A')} //Mudei
            overflowY={"none"}>
            <Stack spacing={8} mx={'auto'} maxW={'lg'} py={1} px={6} >
                <Stack align={'center'}>
                    <Heading fontSize={'4xl'} textAlign={'center'}>
                        Cadastre sua empresa
                    </Heading>
                    <Text fontSize={'lg'} color={'#03C03C'}>
                        e desfrute de todos os benefícios!
                    </Text>
                </Stack>
                <Box
                    rounded={'lg'}
                    bg={useColorModeValue('gray.100', '#000000')} //Mudei
                    boxShadow={'lg'}
                    p={8}
                    border={useColorModeValue("", "1px solid #101010")} //Mudei
                    overflowY={"hidden"}>
                    <Stack spacing={4}>
                        <HStack>
                            <Box>
                                <FormControl isRequired>
                                    <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>Nome da empresa</FormLabel>
                                    <Input
                                        placeholder='Nome da empresa'
                                        type="text"
                                        borderColor={"#343434"} //Mudei
                                        onChange={(e) => setInputs({ ...inputs, name: e.target.value })}
                                        value={inputs.name}
                                        focusBorderColor="#03C03C"
                                        sx={inputStyles}
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
                                        sx={inputStyles}
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
                        <FormControl isRequired isInvalid={!isCNPJValid}>
                            <FormLabel requiredIndicator={<RequiredIndicator color="#03C03C" />}>CNPJ</FormLabel>
                            <Input
                                placeholder='CNPJ da empresa'
                                type="text"
                                value={inputs.cnpj}
                                onChange={handleCNPJChange}
                                borderColor={"#343434"} //Mudei
                                focusBorderColor={isCNPJValid ? "#03C03C" : "red.500"}
                                sx={inputStyles}
                            />
                            {!isCNPJValid && (
                                <FormHelperText color="red.500">CNPJ inválido</FormHelperText>
                            )}
                        </FormControl>
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
                            <Text align={'center'} fontSize={'sm'} color={'#959595'}>
                                Já é um usuário? <Link color={'#03C03C'} onClick={() => setAuthScreen("login")} _hover={{
                                    textDecoration: 'none'
                                }}>Login</Link>
                            </Text>
                        </Stack>
                    </Stack>
                </Box>
            </Stack>
        </Flex>
    );
}