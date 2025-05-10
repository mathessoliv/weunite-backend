/* eslint-disable react/prop-types */
import { Stack, HStack, Text, Box, Flex } from '@chakra-ui/react';
import { Check, X } from "lucide-react";

export const PasswordCriteria = ({ password }) => {
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
                        <X size={16} color="gray" />
                    )}
                    <Text fontSize="xs" color={item.met ? "green.500" : "gray.400"}>{item.label}</Text>
                </HStack>
            ))}
        </Stack>
    );
};

export const PasswordStrengthMeter = ({ password }) => {
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
                <Text fontSize="xs" color="gray.400">Força da senha</Text>
                <Text fontSize="xs" color="gray.400">{getStrengthText(strength)}</Text>
            </Flex>
            <Flex>
                {[...Array(4)].map((_, index) => (
                    <Box
                        key={index}
                        height="2px"
                        width="25%"
                        bg={index < strength ? getColor(strength) : "gray.600"}
                        mr={index < 3 ? 1 : 0}
                    />
                ))}
            </Flex>
            <PasswordCriteria password={password} />
        </Box>
    );
};