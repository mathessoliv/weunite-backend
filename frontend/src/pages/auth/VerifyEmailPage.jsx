import { Button, Flex, FormControl, Heading, Input, Spinner, Text, useColorMode } from "@chakra-ui/react"
import { useEffect, useRef, useState } from "react";
import { Route, Routes, useNavigate, Navigate} from "react-router-dom";
import useShowToast from "../../hooks/useShowToast";
import { useAuthStore } from "../../store/authStore";
import { useSetRecoilState } from "recoil";
import userAtom from "../../atoms/userAtom";
import AuthPage from "./AuthPage";


const VerifyEmailPage = () => {
    const [code, setCode] = useState(["", "", "", "", "", ""]);
    const inputRefs = useRef([]);
    const navigate = useNavigate();
    const showToast = useShowToast();
    const setUser = useSetRecoilState(userAtom);

    const { colorMode } = useColorMode();
    const { isLoading, verifyEmail } = useAuthStore();

    const handleChange = (index, value) => {
        const newCode = [...code];

        // Handle pasted content
        if (value.length > 1) {
            const pastedCode = value.slice(0, 6).split("");
            for (let i = 0; i < 6; i++) {
                newCode[i] = pastedCode[i] || "";
            }
            setCode(newCode);

            // Focus on the last non-empty input or the first empty one
            const lastFilledIndex = newCode.findLastIndex((digit) => digit !== "");
            const focusIndex = lastFilledIndex < 5 ? lastFilledIndex + 1 : 5;
            inputRefs.current[focusIndex].focus();
        } else {
            newCode[index] = value;
            setCode(newCode);

            // Move focus to the next input field if value is entered
            if (value && index < 5) {
                inputRefs.current[index + 1].focus();
            }
        }
    };

    const handleKeyDown = (index, e) => {
        if (e.key === "Backspace" && !code[index] && index > 0) {
            inputRefs.current[index - 1].focus();
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const verificationCode = code.join("");
        try {
            const res = await verifyEmail(verificationCode);
            if (res.success) {
                // Agora podemos salvar o usuário no localStorage, pois ele está verificado
                localStorage.setItem("user-threads", JSON.stringify(res.user));
                setUser(res.user);
                showToast("Sucesso", "Conta ativada com sucesso!", "success");
                navigate("/");
            } else {
                throw new Error(res.message || "Falha na verificação");
            }
        } catch (error) {
            showToast("Erro", "Código inválido ou expirado", "error");
            console.log(error);
        }
    };

    // Auto submit when all fields are filled
    useEffect(() => {
        if (code.every((digit) => digit !== "")) {
            handleSubmit(new Event("submit"));
        }
    }, [code]);


    return (
        <Flex width={"100vw"} height={"100vh"} alignItems={"center"} justifyContent={"center"}
            backgroundColor={colorMode === "dark" ? "#0A0A0A" : "gray.200"}
        >
            <Flex flexDir={"column"} alignItems={"center"} justifyContent={"center"}
                backgroundColor={colorMode === "dark" ? "black" : "gray.100"} //Mudei
                borderRadius="lg" shadow="md" //Mudei
                p={10}
                border={colorMode ==="dark" ? '1px solid #101010' : ''}>

                <Flex flexDir={"column"} gap={3} alignItems={"center"} justifyContent={"center"} mb={1} p={5}>
                    <Heading>Verifique seu email</Heading>
                    <Text color={"#03C03C"}>Insira o código enviado para você</Text>
                </Flex>

                <Flex gap={2}>
                    <FormControl gap={2}>
                        {code.map((digit, index) => (

                            <Input
                                key={index}
                                ref={(el) => (inputRefs.current[index] = el)}
                                type='text'
                                maxLength='6'
                                value={digit}
                                onChange={(e) => handleChange(index, e.target.value)}
                                onKeyDown={(e) => handleKeyDown(index, e)}
                                width={12}
                                height={12}
                                textAlign={"center"}
                                fontWeight={"bold"}
                                border={`1px solid ${digit ? "#03C03C" : "#343434"}`} // Change border color based on input value
                                borderRadius={5}
                                mx={2}
                                focusBorderColor={digit ? "#03C03C" : "#343434"} // Optional: Change border color on focus
                            />

                        ))}
                    </FormControl>
                </Flex>

                <Button type="submit" variant={"outline"} disabled={isLoading || code.some((digit) => !digit)} mt={5}>	{isLoading ? <Spinner size="sm" color="#03C03C" /> : "Cadastrar"}</Button>

                <Button onClick={() => {
                    navigate("../") }} size={"sm"}> 
                
                Voltar

                </Button>

            </Flex>
        </Flex>
    )
}

export default VerifyEmailPage