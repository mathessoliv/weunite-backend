import { Button, Flex, FormControl, Heading, Input, Text, useColorMode } from "@chakra-ui/react"
import { useEffect, useRef, useState } from "react";
import useShowToast from "../../hooks/useShowToast";
import { useAuthStore } from "../../store/authStore";
import { useNavigate } from "react-router-dom";

const VerifyResetCodePage = () => {
    const [code, setCode] = useState(["", "", "", "", "", ""]);
    const inputRefs = useRef([]);
    const showToast = useShowToast();
    const navigate = useNavigate();

    const { colorMode } = useColorMode();

    const { isLoading, verifyResetCode } = useAuthStore();

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
            await verifyResetCode(verificationCode);
            showToast("Successo", "Código verificado com sucesso!", "success");
            navigate(`/reset-password/${verificationCode}`);
        } catch (error) {
            // Reset the code inputs
            setCode(["", "", "", "", "", ""]);
            // Focus on the first input
            inputRefs.current[0].focus();
            // Show error toast
            showToast(
                "Erro", 
                error?.response?.data?.message || "Código inválido ou expirado", 
                "error"
            );
        }
    };

    // Auto submit when all fields are filled
    useEffect(() => {
        if (code.every((digit) => digit !== "")) {
            handleSubmit(new Event("submit"));
        }
    }, [code]);

    return (
        <Flex 
            width={"100vw"} 
            height={"100vh"} 
            alignItems={"center"} 
            justifyContent={"center"}
            backgroundColor={colorMode === "dark" ? "#0A0A0A" : "gray.200"}
        >
            <Flex 
                flexDir={"column"} 
                alignItems={"center"} 
                justifyContent={"center"}   
                backgroundColor={colorMode === "dark" ? "black" : "gray.100"}
                borderRadius="lg" 
                shadow="md"
                p={10} 
                border={colorMode === "dark" ? "1px solid #101010" : ""}
            >
                <Flex 
                    flexDir={"column"} 
                    gap={3} 
                    alignItems={"center"} 
                    justifyContent={"center"} 
                    mb={3} 
                    p={5}
                >
                    <Heading>Verifique seu código</Heading>
                    <Text color={'#03C03C'}>Insira o código enviado para você</Text>
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
                                borderRadius={5}
                                mx={2}
                                focusBorderColor={digit ? "#03C03C" : "#343434"}
                                border={`1px solid ${digit ? "#03C03C" : "#343434"}`}
                            />
                        ))}
                    </FormControl>
                </Flex>

                <Button 
                    type="submit" 
                    variant={"outline"} 
                    disabled={isLoading || code.some((digit) => !digit)} 
                    mt={5}
                >
                    {isLoading ? "Verificando..." : "Verificar Email"}
                </Button>
            </Flex>
        </Flex>
    );
};

export default VerifyResetCodePage;