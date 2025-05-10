import { Flex } from "@chakra-ui/react"; // Importa o componente Flex do Chakra UI para layout flexível
import Header from "../components/header/Header"; // Importa o componente Header
import LeftSideBar from "../components/home/LeftSideBar"; // Importa o componente LeftSideBar
import Home from "../components/home/Home"; // Importa o componente Home
import RightSideBar from "../components/home/RightSideBar"; // Importa o componente RightSideBar

const HomePage = () => {
    return (
        <>
            {/* Renderiza o componente Header */}
            <Header />

            {/* Utiliza Flex para criar um layout flexível e responsivo para a página principal */}
            <Flex w={"100%"}>
                {/* Renderiza o componente LeftSideBar na parte esquerda */}
                <LeftSideBar />
                
                {/* Renderiza o componente Home no meio */}
                <Home />
                
                {/* Renderiza o componente RightSideBar na parte direita */}
                <RightSideBar />
            </Flex>
        </>
    );
}

export default HomePage; // Exporta o componente HomePage para ser utilizado em outras partes da aplicação
