import {
    Button,
    Flex,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    useColorModeValue,
    Avatar,
    Center,
    Link,
    useColorMode,
    IconButton,
    Select,
    useDisclosure,
    Modal,
    ModalOverlay,
    ModalContent,
    ModalHeader,
    ModalBody,
    ModalFooter,
    ModalCloseButton,
    Text
} from '@chakra-ui/react'
import { useRef, useState } from 'react'
import userAtom from '../../atoms/userAtom'
import { useRecoilState } from 'recoil'
import usePreviewImg from '../../hooks/usePreviewImg' // Hook para visualizar imagem de perfil
import useShowToast from '../../hooks/useShowToast' // Hook para exibir mensagens de toast
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import { IoMdArrowRoundBack } from 'react-icons/io'
import AvatarEditor from 'react-avatar-edit'

export default function UpdateProfilePage() {
    const navigate = useNavigate();
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

    // Estado para o usuário e função para atualizar o estado
    const [user, setUser] = useRecoilState(userAtom);

    // Estado para armazenar os dados do formulário
    const [inputs, setInputs] = useState({
        name: user.name,
        username: user.username,
        email: user.email,
        bio: user.bio,
        password: "",
        userType: user.userType, // Adicionado userType ao estado
    });
    // Referência para o input de arquivo (imagem de perfil)
    const fileRef = useRef(null);
    // Estado para controlar se a atualização está em andamento
    const [updating, setUpdating] = useState(false);

    // Função para exibir mensagens de toast
    const showToast = useShowToast();
    // Obtém o modo de cor atual (claro/escuro)
    const { colorMode } = useColorMode();

    // Hook para lidar com a mudança da imagem de perfil

    const { isOpen, onOpen, onClose } = useDisclosure();

    const [src, setSrc] = useState(null); // Mudei
    const [croppedImage, setCroppedImage] = useState(null); // Mudei
    const handleImageChange = (e) => { // Mudei
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setSrc(reader.result); // Mudei
            };
            reader.readAsDataURL(file);
        }
    };

    const [imgUrl, setImgUrl] = useState(null);

    const onSave = (image) => { // Mudei para `onSave`
        setCroppedImage(image);
        setImgUrl(image); // Salva a URL da imagem recortada para envio no formulário
    };
    // Função para lidar com o envio do formulário
    const handleSubmit = async (e) => {
        e.preventDefault(); // Evita o comportamento padrão do formulário
        if (updating) return; // Evita enviar se já estiver atualizando
        setUpdating(true); // Define o estado de atualização como verdadeiro
        try {
            // Envia os dados do formulário para atualizar o perfil do usuário
            const res = await fetch(`/api/users/update/${user._id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ ...inputs, profilePic: imgUrl }) // Agora inclui a URL da imagem recortada
            });
            const data = await res.json();
            if (data.error) {
                showToast("Erro", data.error, "error");
                return;
            }
            showToast("Sucesso", "Perfil atualizado com sucesso", "success");
            setUser(data); // Atualiza o estado do usuário com os dados retornados
            localStorage.setItem("user-threads", JSON.stringify(data)); // Atualiza o armazenamento local

            navigate(`/${inputs.username}`);
        } catch (error) {
            showToast('Erro', error, 'error');
        } finally {
            setUpdating(false); // Define o estado de atualização como falso após a conclusão
        }
    };


    return (
        <form onSubmit={handleSubmit}>
            <Flex
                minH={'100vh'}
                align={'center'}
                justify={'center'}
                bg={useColorModeValue('gray200', '#0A0A0A')} // Define o fundo com base no modo de cor
            >
                <Stack
                    spacing={3}
                    w={'full'}
                    maxW={'md'}
                    bg={useColorModeValue('gray.100', '#000000')} // Define o fundo com base no modo de cor
                    rounded={'xl'} // Cantos arredondados
                    borderRadius="lg" shadow="md" //Mudei
                    p={5} // Padding
                    my={1} // Margem vertical
                    border={colorMode === "dark" ? "1px solid #101010" : ""} // Cor da borda com base no modo de cor
                >

                    <Flex alignItems={"center"} gap={3}>
                        <Flex>
                            <Link as={RouterLink} to={`/${user.username}`}>
                                <IconButton bg={"transparent"} icon={<IoMdArrowRoundBack size={26} cursor={"pointer"} />} />
                            </Link>
                        </Flex>

                        <Flex>
                            <Heading lineHeight={1.1} fontSize={{ base: '2xl', sm: '3xl' }}>
                                Editar Perfil
                            </Heading>
                        </Flex>
                    </Flex>


                    <FormControl>
                        <Stack direction={['column', 'row']} spacing={6}>
                            <Center>
                                <Avatar size="xl"
                                    boxShadow={"md"}
                                    src={imgUrl || user.profilePic} // Usa a imagem de pré-visualização ou a imagem do perfil do usuário
                                >
                                </Avatar>
                            </Center>
                            <Center w="full">
                                {/* Botão para alterar a imagem de perfil */}
                                <Button
                                    w="full"
                                    bg={useColorModeValue('gray.200', '#000000')}
                                    variant={"outline"}
                                    onClick={onOpen}
                                    _hover={{
                                        borderColor: "#03C03C"
                                    }}
                                >
                                    Alterar Avatar
                                </Button>
                                {/* Input de arquivo oculto para seleção da imagem */}
                                <Input type='file' hidden ref={fileRef} onChange={handleImageChange} />
                            </Center>
                        </Stack>
                    </FormControl>

                    <Modal isOpen={isOpen} onClose={onClose}>
                        <ModalOverlay />
                        <ModalContent bg={colorMode === "dark" ? "#000000" : "gray.100"}>
                            <ModalHeader>Editar Avatar</ModalHeader>
                            <ModalCloseButton />
                            <ModalBody>
                                <Text>Escolha sua Foto</Text>
                                <AvatarEditor
                                    width={300}
                                    height={300}
                                    image={src}
                                    onCrop={onSave}
                                />
                            </ModalBody>
                            <ModalFooter>
                            </ModalFooter>
                        </ModalContent>
                    </Modal>


                    {/* Campos de entrada do formulário */}
                    <FormControl>
                        <FormLabel>Nome Completo</FormLabel>
                        <Input
                            type="text"
                            value={inputs.name}
                            onChange={(e) => setInputs({ ...inputs, name: e.target.value })}
                            focusBorderColor='#03C03C' // Cor da borda ao focar
                        />
                    </FormControl>

                    <FormControl>
                        <FormLabel>Nome de usuário</FormLabel>
                        <Input
                            type="text"
                            value={inputs.username}
                            onChange={(e) => setInputs({ ...inputs, username: e.target.value })}
                            focusBorderColor='#03C03C'
                        />
                    </FormControl>

                    <FormControl>
                        <FormLabel>Endereço de email</FormLabel>
                        <Input
                            type="email"
                            value={inputs.email}
                            onChange={(e) => setInputs({ ...inputs, email: e.target.value })}
                            focusBorderColor='#03C03C'
                        />
                    </FormControl>

                    <FormControl>
                        <FormLabel>Bio</FormLabel>
                        <Input
                            type="text"
                            value={inputs.bio}
                            onChange={(e) => setInputs({ ...inputs, bio: e.target.value })}
                            focusBorderColor='#03C03C'
                        />
                    </FormControl>

                    <FormControl>
                        <FormLabel>Senha</FormLabel>
                        <Input
                            placeholder="Senha"
                            type="password"
                            focusBorderColor='#03C03C'
                            color={"#343434"} // Cor do texto
                            sx={inputStyles}
                        />
                    </FormControl>

                    {/* Botões para cancelar e submeter o formulário */}
                    <Stack spacing={3} direction={'row'}>
                        <Link as={RouterLink} to={`/${user.username}`} style={{ width: "100%" }}>
                            <Button
                                bg={'#dd0600'}
                                color={'white'}
                                w="full"
                                _hover={{
                                    opacity: 0.8, // Opacidade ao passar o mouse
                                }}
                                transition={"0.3s"} // Transição suave
                            >
                                Cancelar
                            </Button>
                        </Link>

                        <Button
                            bg={'#03C03C'}
                            color={'#000000'}
                            w="full"
                            _hover={{
                                opacity: 0.8,
                            }}
                            transition={"0.3s"}
                            type='submit'
                            isLoading={updating} // Indica se o formulário está sendo enviado
                        >
                            Enviar
                        </Button>
                    </Stack>
                </Stack>
            </Flex>
        </form>
    )
}
