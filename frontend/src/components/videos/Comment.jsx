import { DeleteIcon } from '@chakra-ui/icons';
import { Avatar, Divider, Flex, Link, Text } from '@chakra-ui/react';
import { Link as RouterLink } from 'react-router-dom';
import useShowToast from '../../hooks/useShowToast';

const Comment = ({ reply, lastReply, post, currentUser, onCommentDeleted }) => {
    const showToast = useShowToast();

    const handleDeleteComment = async () => {

        // Deletar post
        if (!window.confirm("Você tem certeza que quer deletar este post?")) return;

        if (currentUser._id !== reply.userId) {
            showToast("Error", 'Não autorizado', 'error');
            return;
        } 

        try {
            const res = await fetch(`/api/posts/${post._id}/comments/${reply._id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    // Adicione aqui o header de autenticação, se necessário
                    // 'Authorization': `Bearer ${seu_token_aqui}`
                }
            });

            const data = await res.json();
            if (!res.ok) {
                throw new Error(data.error || 'Ocorreu um erro ao deletar o comentário');
            }
            
            showToast("Sucesso", 'Comentário deletado', 'success');

            // Chama a função de callback para atualizar o estado no componente pai
            if (onCommentDeleted) {
                onCommentDeleted(reply._id);
            }

        } catch (error) {
            showToast("Error", 'Erro ao deletar comentário', 'error');
        }
    };

    return (
        <>
            <Flex w="100%">

                {/* container comentário*/}
                <Flex justifyContent={'space-between'} flexDir={'row'} flex={1}>

                        {/* Avatar, nome do usuário e texto do comentário */}
                        <Link as={RouterLink} to={`/${reply.username}`} style={{ textDecoration: 'none' }}>

                            {/* Avatar e nome do usuário */}
                            <Flex flexDirection="row" gap={3} mt={4} alignItems="center">
                                <Flex>
                                    <Avatar src={reply.userProfilePic} size="md" cursor="pointer" />
                                </Flex>

                                <Flex>
                                    <Text cursor="pointer">{reply.username}</Text>
                                </Flex>
                            </Flex>

                            {/* Texto do comentário */}
                            <Flex>
                                <Text mt={4}>{reply.text}</Text>
                            </Flex>
                        
                        </Link> {/* Fim do link com avatar, nome do usuário e comentário*/}
                    
                    {currentUser._id === reply.userId && (
                        <Flex>
                            <DeleteIcon
                                size={20}
                                onClick={handleDeleteComment}
                                cursor="pointer"
                                mt={5}
                                mr={3}
                            />
                        </Flex>
                    )}

                </Flex> {/* Fim do container comentário*/}

            </Flex>

            {/* Divider condicional para separar comentários */}
            {!lastReply ? <Divider mt={4} /> : null}
            
        </>
    );
};

export default Comment;
