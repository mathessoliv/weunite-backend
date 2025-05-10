/* eslint-disable react/prop-types */
import { Avatar, Flex, Link, Text, useColorMode, useColorModeValue } from '@chakra-ui/react';
import { Link as RouterLink } from 'react-router-dom';

const ProfileComment = ({ user, comment }) => {
    const colorMode = useColorMode();
    const textColor = useColorModeValue('black', 'white'); //Mudei
    return (
        <>
            <Flex w="100%" flexDirection="column"
                borderRadius="lg" shadow="md" //Mudei
                bg={colorMode.colorMode === "dark" ? "#000000" : "gray.100"} // Mudei
                border={colorMode.colorMode === "dark" ? "1px solid #101010" : ""} //Mudei
                mt={2} 
                p={5}>
                <Text>Em resposta a{" "}
                    <Link as={RouterLink} to={`/${comment.postAuthorName}/post/${comment.postId}`} style={{ color: '#03C03C' }}>
                        {comment.postAuthorName}
                    </Link>
                </Text>
                {/* Link para o perfil do usuário */}
                <Link as={RouterLink} to={`/${comment.postAuthorName}/post/${comment.postId}`} style={{ textDecoration: 'none' }}>
                    <Flex flexDirection="row" gap={3} mt={4} alignItems="center">
                        {/* Imagem de perfil do usuário */}
                        <Avatar src={comment.profilePic} size="md" cursor="pointer" />
                        {/* Nome de usuário */}
                        <Text cursor="pointer">{user.username}</Text>
                    </Flex>
                </Link>

                {/* Comentário do usuário */}
                <Text mt={4}>{comment.text}</Text>
            </Flex>
        </>
    );
};

export default ProfileComment;
