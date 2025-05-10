import { AddIcon } from "@chakra-ui/icons";
import {
    Button,
    CloseButton,
    Flex,
    FormControl,
    Image,
    Input,
    Modal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalFooter,
    ModalHeader,
    ModalOverlay,
    Text,
    Textarea,
    useColorMode,
    useDisclosure,
} from "@chakra-ui/react";
import { useRef, useState } from "react";
import usePreviewImg from "../../hooks/usePreviewImg";
import { BsFillImageFill } from "react-icons/bs";
import { useRecoilState, useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";
import useShowToast from "../../hooks/useShowToast";
import oportunitiesAtom from "../../atoms/oportunitiesAtom";
import { useParams } from "react-router-dom";


// Constante para definir o número máximo de caracteres permitidos
const MAX_CHAR = 500;
const MAX_CHAR_TITLE = 80;
const MAX_CHAR_LOCATION = 50;


const CreatePost = () => {
    const { colorMode } = useColorMode();
    const { isOpen, onOpen, onClose } = useDisclosure();
    const [oportunityText, setOportunityText] = useState(''); // Estado para armazenar o texto da oportunidade
    const [oportunityTitle, setOportunityTitle] = useState(''); // Estado para armazenar o título da oportunidade
    const [oportunityLocation, setOportunityLocation] = useState(''); // Estado para armazenar a localização da oportunidade
    const { handleImageChange, imgUrl, setImgUrl } = usePreviewImg(); // Hook personalizado para lidar com a visualização de imagem
    const imageRef = useRef(null); // Referência para o input de imagem
    const [remainingChar, setRemainingChar] = useState(MAX_CHAR); // Estado para controlar o número de caracteres restantes para o texto
    const [remainingOportunityChar, setRemainingOportunityChar] = useState(MAX_CHAR_TITLE); // Estado para controlar o número de caracteres restantes para o título
    const [remainingLocationChar, setRemainingLocationChar] = useState(MAX_CHAR_LOCATION); // Estado para controlar o número de caracteres restantes para a localização
    const user = useRecoilValue(userAtom); // Valor do usuário atual obtido do estado global
    const showToast = useShowToast(); // Função para mostrar mensagens de toast
    const [updating, setUpdating] = useState(false); // Estado para controlar o status de atualização
    const [oportunities, setOportunities] = useRecoilState(oportunitiesAtom); // Estado global para a lista de oportunidades
    const { username } = useParams(); // Parâmetro da URL para o nome de usuário
    const [selectedDate, setSelectedDate] = useState("");
    const [maxApplications, setMaxApplications] = useState('');

    // Função para lidar com mudanças no texto da oportunidade
    const handleTextChange = (e) => {
        const inputText = e.target.value;

        if (inputText.length > MAX_CHAR) {
            // Trunca o texto se exceder o limite
            const truncatedText = inputText.slice(0, MAX_CHAR);
            setOportunityText(truncatedText);
            setRemainingChar(0);
        } else {
            setOportunityText(inputText);
            setRemainingChar(MAX_CHAR - inputText.length);
        }
    };

    // Função para lidar com mudanças no título da oportunidade
    const handleTitleChange = (e) => {
        const inputTitle = e.target.value;

        if (inputTitle.length > MAX_CHAR_TITLE) {
            // Trunca o texto se exceder o limite
            const truncatedText = inputTitle.slice(0, MAX_CHAR_TITLE);
            setOportunityTitle(truncatedText);
            setRemainingOportunityChar(0);
        } else {
            setOportunityTitle(inputTitle);
            setRemainingOportunityChar(MAX_CHAR_TITLE - inputTitle.length);
        }
    };

    // Função para lidar com mudanças na localização da oportunidade
    const handleLocationChange = (e) => {
        const inputLocation = e.target.value;

        if (inputLocation.length > MAX_CHAR_LOCATION) {
            // Trunca o texto se exceder o limite
            const truncatedText = inputLocation.slice(0, MAX_CHAR_LOCATION);
            setOportunityLocation(truncatedText);
            setRemainingLocationChar(0);
        } else {
            setOportunityLocation(inputLocation);
            setRemainingLocationChar(MAX_CHAR_LOCATION - inputLocation.length);
        }
    };

    const handleDateChange = (e) => {
        setSelectedDate(e.target.value);
    };

    // Função para criar uma nova oportunidade
    const handleCreateOportunity = async () => {
        setUpdating(true); // Inicia o estado de atualização
        try {
            const res = await fetch("/api/oportunities/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    postedBy: user._id,
                    title: oportunityTitle,
                    text: oportunityText,
                    img: imgUrl,
                    location: oportunityLocation, // Adiciona a localização
                    applicationDeadline: selectedDate,
                    maxApplications: parseInt(maxApplications)
                }),
            });

            const data = await res.json();
            if (data.error) {
                showToast("Error", data.error, "error");
                return;
            }
            
            showToast("Sucesso", "Oportunidade criada com sucesso", "success");

            // Atualiza a lista de oportunidades se o usuário for o mesmo
            if (username === user.username) {
                setOportunities([data, ...oportunities]);
            }

            // Fecha o modal e limpa os estados
            onClose();
            setOportunityText("");
            setOportunityTitle("");
            setOportunityLocation("");
            setImgUrl("");
            setMaxApplications('');
        } catch (error) {
            showToast("Error", error.message, "error"); // Exibe mensagem de erro
        } finally {
            setUpdating(false); // Finaliza o estado de atualização
        }
    };

    return (
        <>
            {/* Botão flutuante para abrir o modal de criação de oportunidade */}
            <Button
                position={"fixed"}
                bottom={10}
                right={10}
                bg={colorMode === "dark" ? "#000000" : "#f8fafc"}
                variant={"outline"}
                _hover={{
                    borderColor: "#03C03C",
                    bg: colorMode === "dark" ? "#000000" : "#FFFFFF" //Mudei
                  }}
                zIndex={50}
                onClick={onOpen}
            >
                <AddIcon />
            </Button>

            {/* Modal para criar uma nova oportunidade */}
            <Modal isOpen={isOpen} onClose={onClose}>
                <ModalOverlay />
                <ModalContent
                  bg={colorMode === "dark" ? "black" : "gray.100"} //Mudei 
                  color={colorMode === "dark" ? "white" : "black"} //Mudei
                >
                    <ModalHeader>Criar oportunidade</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody pb={6}>
                        <FormControl>
                            {/* Área de texto para o título da oportunidade */}
                            <Textarea
                                borderColor={"#343434"} //Mudei
                                placeholder="Título da oportunidade"
                                onChange={handleTitleChange}
                                value={oportunityTitle}
                                focusBorderColor="#03C03C"
                            />
                            <Text
                                fontSize={"xs"}
                                fontWeight={"bold"}
                                textAlign={"right"}
                                m={1}
                                color={"#343434"}
                            >
                                {remainingOportunityChar}/{MAX_CHAR_TITLE}
                            </Text>
                            {/* Área de texto para o conteúdo da oportunidade */}
                            <Textarea
                             borderColor={"#343434"} //Mudei
                                placeholder="Conteúdo da oportunidade"
                                onChange={handleTextChange}
                                value={oportunityText}
                                focusBorderColor="#03C03C"
                            />
                            <Text
                                fontSize={"xs"}
                                fontWeight={"bold"}
                                textAlign={"right"}
                                m={1}
                                color={"#343434"}
                            >
                                {remainingChar}/{MAX_CHAR}
                            </Text>

                            {/* Área de texto para a localização da oportunidade */}
                            <Textarea
                             borderColor={"#343434"} //Mudei
                                placeholder="Local da oportunidade"
                                onChange={handleLocationChange}
                                value={oportunityLocation}
                                focusBorderColor="#03C03C"
                            />
                            <Text
                                fontSize={"xs"}
                                fontWeight={"bold"}
                                textAlign={"right"}
                                m={1}
                                color={"#343434"}
                            >
                                {remainingLocationChar}/{MAX_CHAR_LOCATION}
                            </Text>

                            <Input
                             borderColor={"#343434"} //Mudei
                            placeholder="Número de candidatos"
                            type="number" 
                            value={maxApplications} 
                            onChange={(e) => setMaxApplications(e.target.value)}
                            mb={5}
                            />

                            <Input
                             borderColor={"#343434"} //Mudei
                                type="date"
                                value={selectedDate}
                                onChange={handleDateChange}
                                focusBorderColor="#03C03C"
                                mb={3}
                            />

                            {/* Input para seleção de imagem */}
                            <Input
                                type="file"
                                hidden
                                ref={imageRef}
                                onChange={handleImageChange}
                            />
                            <BsFillImageFill
                                style={{ marginLeft: "5px", cursor: "pointer" }}
                                size={16}
                                onClick={() => imageRef.current.click()}
                            />
                        </FormControl>

                        {/* Exibição da imagem selecionada, se houver */}
                        {imgUrl && (
                            <Flex mt={5} width={"full"} pos={"relative"}>
                                <Image src={imgUrl} alt="Imagem escolhida" />
                                <CloseButton
                                    onClick={() => setImgUrl("")}
                                    pos={"absolute"}
                                    top={2}
                                    right={2}
                                />
                            </Flex>
                        )}
                    </ModalBody>

                    <ModalFooter>
                        <Button
                            color="#000000"
                            mr={3}
                            onClick={handleCreateOportunity}
                            isLoading={updating}
                            bg={"#03C03C"}
                            _hover={{ opacity: 0.8 }}
                            transition={0.3}
                        >
                            Publicar
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
        </>
    );
};

export default CreatePost;
