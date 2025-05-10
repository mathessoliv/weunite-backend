import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import { Avatar, Box, Flex, IconButton, Input, Link, Spacer, Text, useColorMode, Image } from "@chakra-ui/react";
import "../../index.css";
import { IoHomeOutline } from "react-icons/io5";
import { BsPeople } from "react-icons/bs";
import { TfiLink } from "react-icons/tfi";
import { IconContext } from "react-icons";
import LogoutButton from "./Logout";
import Notificacao from "./Notificacao";
import { useRecoilValue } from "recoil";
import userAtom from "../../atoms/userAtom";
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useEffect, useRef, useState } from "react";
import { IoChatbubblesOutline } from "react-icons/io5";

const Header = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const [isLoading, setIsLoading] = useState(false);
  const user = useRecoilValue(userAtom);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isInputActive, setIsInputActive] = useState(false);
  const inputRef = useRef(null);
  const resultsRef = useRef(null);
  const navigate = useNavigate();

  const iconColor = colorMode === "dark" ? "#FFFFFF" : "#000000";
  const getIconColor = (path) => (location.pathname === path ? "#22C55E" : iconColor);

  useEffect(() => {
    if (query.length > 0) {
      setIsLoading(true);
      const searchUsers = async () => {
        try {
          const response = await fetch(`/api/users/search?query=${encodeURIComponent(query)}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
            },
          });

          if (!response.ok) {
            throw new Error('Network response was not ok');
          }

          const data = await response.json();
          setResults(data);
        } catch (error) {
          console.error('Error searching users:', error);
        } finally {
          setIsLoading(false);
        }
      };

      const debounce = setTimeout(() => {
        searchUsers();
      }, 300);

      return () => clearTimeout(debounce);
    } else {
      setResults([]);
      setIsLoading(false);
    }
  }, [query]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (inputRef.current && !inputRef.current.contains(event.target) &&
        resultsRef.current && !resultsRef.current.contains(event.target)) {
        setIsInputActive(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const shouldShowResults = isInputActive && query.length > 0;

  useEffect(() => {
    const handleKeyPress = (event) => {

      if (event.ctrlKey && event.key === "4") {
        event.preventDefault();
        navigate(`/${user.username}`);
      }

      if (event.ctrlKey && event.key === "3") {
        event.preventDefault();
        navigate("/chat");
      }

      if (event.ctrlKey && event.key === "2") {
        event.preventDefault();
        navigate("/oportunities");
      }

      if (event.ctrlKey && event.key === "1") {
        event.preventDefault();
        navigate("/");
      }

      if (event.ctrlKey && (event.key === "p" || event.key === "P")) {
        event.preventDefault();
        inputRef.current.focus();
      }

      if (event.ctrlKey && (event.key === "t" || event.key === "T")) {
        event.preventDefault();
        inputRef.current.focus();
      }
    };


    document.addEventListener("keydown", handleKeyPress);


    return () => {
      document.removeEventListener("keydown", handleKeyPress);
    };
  }, [navigate]);

  useEffect(() => {
    const handleKeyPress = (event) => {
      if (event.ctrlKey && event.key.toLowerCase() === "m") {
        event.preventDefault(); // Impede a abertura de uma nova aba
        toggleColorMode(); // Alterna o tema
      }
    };

    window.addEventListener("keydown", handleKeyPress);

    return () => {
      window.removeEventListener("keydown", handleKeyPress);
    };
  }, [toggleColorMode]);



  return (
    <>
      <Box
        position={"sticky"}
        top={0}
        zIndex={60}
        backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"} //Mudei
        boxShadow={"0 2px 4px rgba(0, 0, 0, 0.1)"}
      >
        <Flex
          display={"flex"}
          alignItems={"center"}
          m={0}
          pt={{ base: 3, md: 8, lg: 6 }}
          pl={{ base: 4, md: 8, lg: 6 }}
          pr={{ base: 4, md: 8, lg: 6 }}
          position="relative" //Mudei
          justifyContent="space-between" // Mudei
        >

          <Box left="10%" transform="translateX(25%)"> {/*Mudei */}
            <Box maxH={"0"} display="flex" pt={2}
              justifyContent="center" alignItems="center" flex={1}
              pb={3}>
              <Link as={RouterLink} to={`/`}>
                <Image
                  src={colorMode === "dark" ? "/public/WeUP.png" : "/public/WeUB.png"}
                  alt='Logo'
                  width="185px"  // Define a largura da imagem
                  height="auto"  // Mantém a proporção original da imagem
                  fontSize={{ base: "25px", md: "30px", lg: "35px" }}
                  maxW={{ base: "100px", md: "150px", lg: "200px" }}
                  mt={4}
                />
              </Link>
            </Box>
          </Box>

          <Spacer />

          {/*Mudei */}
          <Box position="absolute" left="50%" transform="translateX(-50%)" width={"40vw"}>

            <Input
              ref={inputRef}
              placeholder={isInputActive ? "" : "Procure por usuários"}
              variant={"outline"}
              width={{ base: "36vw", lg: "40vw" }}
              borderColor={colorMode === "dark" ? "#343434" : "#959595"}
              focusBorderColor="#03C03C"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onFocus={() => setIsInputActive(true)}
              color={colorMode === "dark" ? "#FFFFFF" : "#000000"}  //Mudei
              _placeholder={colorMode === "dark" ? "#FFFFFF" : "#000000"} //Mudei
            />

            {shouldShowResults && (
              <Box
                ref={resultsRef}
                position="absolute"
                left="50%"
                transform="translateX(-50%)"
                width={{ base: "70vw", sm: "50vw", md: "50vw", lg: "30vw" }}
                maxHeight="300px"
                overflowY="auto"
                borderRadius="lg" shadow="md" //Mudei
                mt={3}
                backgroundColor={colorMode === "dark" ? "#0A0A0A" : "#FFFFFF"} //Mudei
                border={colorMode === "dark" ? "1px solid #343434" : ""}
                zIndex={1000}
              >
                {isLoading ? (
                  <Flex justifyContent="center" alignItems="center" minHeight="150px">
                    <Text>Loading...</Text>
                  </Flex>
                ) : results.length > 0 ? (
                  results.map((user) => (
                    <Link
                      key={user._id}
                      as={RouterLink}
                      to={user.username}
                      style={{ textDecoration: "none" }}
                      onMouseDown={(e) => e.stopPropagation()}
                    >
                      <Flex gap={3} alignItems="center" p={3}
                        _hover={{
                          backgroundColor: colorMode === "dark" ? "#121212" : "gray.200", //Mudei
                          borderRadius: "md"
                        }}
                      >
                        <Avatar src={user.profilePic} alt={user.username} />
                        <Box>
                          <Text fontWeight="bold">{user.name}</Text>
                          <Text>{user.username}</Text>
                        </Box>
                      </Flex>
                    </Link>
                  ))
                ) : (
                  <Flex justifyContent="center" alignItems="center" minHeight="150px">
                    <Text>Nenhum usuário encontrado</Text>
                  </Flex>
                )}
              </Box>
            )}
          </Box>

          <Spacer />

          <Box display={"flex"} alignItems={"center"} gap={4}>
            <Box display={"flex"} alignItems={"center"} gap={4}>
              <Notificacao />
              <IconButton
                boxSize={{ base: 6, md: 8, lg: 10 }} //Mudei
                fontSize={{ base: 8, md: 13, lg: 15 }}
                icon={colorMode === "dark" ? <SunIcon /> : <MoonIcon />}
                onClick={toggleColorMode}
                variant="outline"
                borderColor={colorMode === "dark" ? "#343434" : "#959595"}
                _hover={{
                  borderColor: "#03C03C",
                  bg: colorMode === "dark" ? "#000000" : "" //Mudei
                }}
              />
              <LogoutButton />

            </Box>
          </Box>
        </Flex>

        <IconContext.Provider value={{ size: "auto" }}>
          <Flex
            display={"flex"}
            alignItems={"center"}
            justifyContent={"center"}
            gap={{ base: 10, md: 20, lg: 20 }}
            //borderBottom={colorMode === "dark" ? "1px solid #343434" : "1px solid #gray.200"} //Mudei
            m={0}
            pt={2}
            pb={3}
            zIndex={60}
            //  shadow= {colorMode === "dark" ? " 0px 0px 6px -1px rgba(77,77,77,1)" : "md"} //Mudei
            backgroundColor={colorMode === "dark" ? "#000000" : "gray.100"} //Mudei
          >
            {user && (
              <Link as={RouterLink} to={`/`}>
                <IoHomeOutline cursor={"pointer"} size={25} color={getIconColor("/")} />
              </Link>
            )}

            {user && (
              <Link as={RouterLink} to={`/oportunities`}>
                <TfiLink  size={25} cursor={"pointer"} color={getIconColor("/oportunities")}/>
                
              </Link>
            )}

            {user && (
              <Link as={RouterLink} to={`/chat`}>
                <IoChatbubblesOutline size={25} cursor={"pointer"} color={getIconColor("/chat")} />
              </Link>
            )}
          </Flex>
        </IconContext.Provider>
      </Box>
    </>
  );
};

export default Header;