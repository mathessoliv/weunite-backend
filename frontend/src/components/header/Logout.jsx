import { Avatar, Link, Menu, MenuButton, MenuItem, MenuList, useColorMode } from "@chakra-ui/react"
import { useRecoilValue, useSetRecoilState } from "recoil";
import userAtom from "../../atoms/userAtom";
import useShowToast from "../../hooks/useShowToast";
import { Link as RouterLink, useNavigate } from "react-router-dom";

// Componente para o botão de logout
const LogoutButton = () => {
    // Hook para acessar o modo de cor (claro ou escuro)
    const { colorMode } = useColorMode();

    // Função personalizada para mostrar toast
    const showToast = useShowToast();

    // Funções para manipular o estado global com Recoil
    const setUser = useSetRecoilState(userAtom);
    const user = useRecoilValue(userAtom);
    const navigate = useNavigate();

    // Função para lidar com o logout
    const handleLogout = async () => {
        try {
            // Envia a requisição de logout para a API
            const res = await fetch("/api/auth/logout", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            })

            // Converte a resposta da API para JSON
            const data = await res.json();

            // Se houver um erro, mostra um toast com a mensagem de erro
            if (data.error) {
                showToast("Error", data.error, "error")
                return;
            }

            // Remove as informações do usuário do localStorage e atualiza o estado global
            localStorage.removeItem("user-threads");
            setUser(null);
            navigate(`/auth`);
        } catch (error) {
            // Em caso de exceção, mostra um toast com a mensagem de erro
            showToast("Error", error.toString(), "error")
        }
    };

    return (
        <Menu> 
            {/* Botão do menu, exibido como um Avatar */}                           {/*Mudei a size */}
            <MenuButton as={Avatar} src={user.profilePic} size={{ base: "md", md: "md", lg: "md" }} cursor={"pointer"} border={"1px solid #343434"}></MenuButton>

            {/* Lista do menu */}
            <MenuList backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"} borderColor={colorMode === "dark" ? "#343434" : "#000000"}>
                {/* Link para o perfil do usuário */}
                <Link as={RouterLink} to={`/${user.username}`} style={{ textDecoration: 'none' }}>

                    {/*Mudei */}
                    <MenuItem display={"flex"} justifyContent={"center"} backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"}
                     _hover={{
                        color: "#03C03C",
                      }}
                    >
                        Perfil
                    </MenuItem>
                </Link>

                

                 {/*Mudei */}
                <MenuItem display={"flex"} justifyContent={"center"} backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"}
                 _hover={{
                    color: "#03C03C",
                  }}
                >
                    Configurações
                </MenuItem>

               

                {/* Item de logout, chama a função de logout ao ser clicado */}
                 {/*Mudei */}
                <MenuItem display={"flex"} justifyContent={"center"} backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"} onClick={handleLogout}
                _hover={{
                    color: "#03C03C",
                  }}>
                    Sair
                </MenuItem>

            </MenuList>
        </Menu>
    )
}

export default LogoutButton
