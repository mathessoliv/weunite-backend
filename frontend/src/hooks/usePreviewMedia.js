import { useToast } from "@chakra-ui/react";
import { useState } from "react";

const usePreviewMedia = () => {
    const [mediaUrl, setMediaUrl] = useState(null);
    const [mediaType, setMediaType] = useState('none');
    const toast = useToast();

    const handleMediaChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Verificar tamanho do arquivo (limite de 100MB)
        const maxSize = 100 * 1024 * 1024; // 100MB em bytes
        if (file.size > maxSize) {
            toast({
                title: "Erro",
                description: "O arquivo é muito grande. O tamanho máximo é 100MB",
                status: "error",
                duration: 3000,
                isClosable: true,
            });
            return;
        }

        if (file.type.startsWith("image/")) {
            handleImageFile(file);
        } else if (file.type.startsWith("video/")) {
            handleVideoFile(file);
        } else {
            toast({
                title: "Erro",
                description: "Por favor, selecione um arquivo de imagem ou vídeo",
                status: "error",
                duration: 3000,
                isClosable: true,
            });
            setMediaUrl(null);
            setMediaType('none');
        }
    };

    const handleImageFile = (file) => {
        const reader = new FileReader();
        reader.onloadend = () => {
            setMediaUrl(reader.result);
            setMediaType('image');
        };
        reader.readAsDataURL(file);
    };

    const handleVideoFile = (file) => {
        const reader = new FileReader();
        reader.onloadend = () => {
            setMediaUrl(reader.result);
            setMediaType('video');
        };
        reader.readAsDataURL(file);
    };

    return { handleMediaChange, mediaUrl, setMediaUrl, mediaType, setMediaType };
};

export default usePreviewMedia;