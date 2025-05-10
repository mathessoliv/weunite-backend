/* eslint-disable react/prop-types */
import {
    Modal,
    ModalOverlay,
    ModalContent,
    ModalHeader,
    ModalBody,
    ModalCloseButton,
    Button,
    Checkbox,
    Text,
    Box,
    useDisclosure,
    Tabs,
    TabList,
    TabPanels,
    Tab,
    TabPanel,
    Flex,
    Divider,
    List,
    ListItem,
    Icon,
    Link,
    useColorMode
} from '@chakra-ui/react';
import { FaPhoneAlt, FaEnvelope, FaFacebook, FaInstagram, FaTwitter } from 'react-icons/fa';


const TermsModal = ({ isOpen, onClose }) => {
    const { colorMode } = useColorMode(); // Mudei
    return (
        <Modal isOpen={isOpen} onClose={onClose} size="4xl" scrollBehavior="inside">
            <ModalOverlay bg="blackAlpha.700" backdropFilter="blur(10px)" />
            <ModalContent bg={colorMode === "dark" ? "black" : "gray.100"} border="1px solid #343434">
                <ModalHeader borderBottom="1px solid #343434">
                    <Text color={colorMode === "dark" ? "white" : "black"}>Termos e Políticas</Text>
                </ModalHeader>
                <ModalCloseButton color={colorMode === "dark" ? "white" : "black"} />
                <ModalBody py={6}>
                    <Tabs variant="soft-rounded" colorScheme="green">
                        <TabList mb={4} gap={2}>
                            <Tab color={colorMode === "dark" ? "white" : "black"} _selected={{ bg: "#03C03C", color: "white" }}>Termos de Uso</Tab>
                            <Tab color={colorMode === "dark" ? "white" : "black"} _selected={{ bg: "#03C03C", color: "white" }}>Política de Privacidade</Tab>
                            <Tab color={colorMode === "dark" ? "white" : "black"} _selected={{ bg: "#03C03C", color: "white" }}>Política de Cookies</Tab>
                            <Tab color={colorMode === "dark" ? "white" : "black"} _selected={{ bg: "#03C03C", color: "white" }}>Suporte</Tab>
                        </TabList>
                        <TabPanels>
                            <TabPanel>
                                <Box color={colorMode === "dark" ? "white" : "black"}>
                                    <Text fontSize="xl" fontWeight="bold" mb={4}>Termos de Uso</Text>
                                    <Text mb={4}>Última atualização: 15 de novembro de 2024</Text>
                                    <Divider mb={4} />

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>1. Introdução</Text>
                                    <Text mb={4}>Bem-vindo à nossa plataforma, uma rede social dedicada a conectar atletas, clubes e entidades esportivas. Ao acessar ou usar nossa plataforma, você concorda com estes Termos de Uso.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>2. Elegibilidade</Text>
                                    <Text mb={4}>• Você deve ter pelo menos 13 anos de idade.<br />
                                        • Entre 13-18 anos, é necessária permissão de responsável legal.<br />
                                        • Perfis verificados requerem documentação adicional.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>3. Uso da Plataforma</Text>
                                    <Text mb={4}>• Você se compromete a não usar a plataforma para qualquer atividade ilegal ou que infrinja os direitos de outros usuários.<br />
                                        • A conta é pessoal e intransferível, sendo sua responsabilidade manter a segurança da sua conta e senha.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>4. Direitos de Propriedade Intelectual</Text>
                                    <Text mb={4}>• Todo o conteúdo da plataforma, incluindo textos, imagens, vídeos e software, é protegido por direitos autorais e não pode ser reproduzido sem permissão.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>5. Limitação de Responsabilidade</Text>
                                    <Text mb={4}>• Não nos responsabilizamos por qualquer dano ou perda de dados decorrente do uso da plataforma.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>6. Modificações</Text>
                                    <Text mb={4}>• Podemos modificar ou descontinuar a plataforma a qualquer momento, sem aviso prévio.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>7. Contato</Text>
                                    <Text mb={4}>• Em caso de dúvidas sobre os Termos de Uso, entre em contato com a nossa equipe de suporte.</Text>
                                </Box>
                            </TabPanel>
                            <TabPanel>
                                <Box color={colorMode === "dark" ? "white" : "black"}>
                                    <Text fontSize="xl" fontWeight="bold" mb={4}>Política de Privacidade</Text>
                                    <Text mb={4}>Última atualização: 15 de novembro de 2024</Text>
                                    <Divider mb={4} />

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>1. Informações Coletadas</Text>
                                    <Text mb={4}>Coletamos informações que você fornece diretamente, incluindo dados pessoais como nome, e-mail, histórico esportivo, conquistas, e informações sobre o uso da plataforma.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>2. Como Usamos as Informações</Text>
                                    <Text mb={4}>• Utilizamos suas informações para melhorar a sua experiência na plataforma, fornecer conteúdos personalizados, e comunicar atualizações sobre nossa rede social.<br />
                                        • Não compartilhamos suas informações pessoais com terceiros sem o seu consentimento, exceto quando necessário para cumprir obrigações legais.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>3. Retenção de Dados</Text>
                                    <Text mb={4}>• Seus dados são retidos enquanto sua conta estiver ativa. Caso você deseje excluir sua conta, entre em contato conosco para remover seus dados.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>4. Segurança</Text>
                                    <Text mb={4}>• Implementamos medidas de segurança para proteger suas informações pessoais, incluindo criptografia e controle de acesso restrito.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>5. Seus Direitos</Text>
                                    <Text mb={4}>• Você tem o direito de acessar, corrigir ou excluir suas informações pessoais. Para isso, entre em contato com a nossa equipe de suporte.</Text>
                                </Box>
                            </TabPanel>
                            <TabPanel>
                                <Box color={colorMode === "dark" ? "white" : "black"}>
                                    <Text fontSize="xl" fontWeight="bold" mb={4}>Política de Cookies</Text>
                                    <Text mb={4}>Última atualização: 15 de novembro de 2024</Text>
                                    <Divider mb={4} />

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>1. O que são Cookies</Text>
                                    <Text mb={4}>Cookies são pequenos arquivos de texto armazenados em seu dispositivo quando você usa nossa plataforma. Eles são usados para melhorar a experiência de navegação.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>2. Como Usamos os Cookies</Text>
                                    <Text mb={4}>• Usamos cookies para lembrar preferências, como idioma e configurações de visualização.<br />
                                        • Também usamos cookies para analisar o tráfego e melhorar o desempenho da plataforma.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>3. Tipos de Cookies</Text>
                                    <Text mb={4}>• Cookies necessários: Essenciais para o funcionamento da plataforma.<br />
                                        • Cookies de desempenho: Coletam dados sobre como a plataforma é usada.<br />
                                        • Cookies de funcionalidade: Lembram de suas preferências e personalizam sua experiência.<br />
                                        • Cookies de publicidade: Usados para fornecer anúncios personalizados.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>4. Controle sobre Cookies</Text>
                                    <Text mb={4}>• Você pode controlar e excluir cookies a qualquer momento através das configurações do seu navegador. No entanto, desabilitar cookies pode afetar a funcionalidade da plataforma.</Text>

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>5. Alterações na Política de Cookies</Text>
                                    <Text mb={4}>• Podemos atualizar nossa Política de Cookies de tempos em tempos. Quaisquer alterações serão publicadas aqui.</Text>
                                </Box>
                            </TabPanel>
                            <TabPanel>
                                <Box color={colorMode === "dark" ? "white" : "black"}>
                                    <Text fontSize="xl" fontWeight="bold" mb={4}>Suporte</Text>
                                    <Text mb={4}>Última atualização: 15 de novembro de 2024</Text>
                                    <Divider mb={4} />

                                    <Text fontWeight="bold" fontSize="lg" mb={2}>1. Contatos</Text>
                                    <Text mb={4}>Se você precisar de suporte ou tiver alguma dúvida, entre em contato com nossa equipe de atendimento ao cliente através dos seguintes canais:</Text>
                                    <List spacing={3}>
                                        <ListItem>
                                            <Flex align="center">
                                                <Icon as={FaPhoneAlt} boxSize={5} color="green.400" />
                                                <Text ml={3}>Telefone: (11) 1234-5678</Text>
                                            </Flex>
                                        </ListItem>
                                        <ListItem>
                                            <Flex align="center">
                                                <Icon as={FaEnvelope} boxSize={5} color="green.400" />
                                                <Text ml={3}>E-mail: suporte@weunite.com.br</Text>
                                            </Flex>
                                        </ListItem>
                                        <ListItem>
                                            <Flex align="center">
                                                <Icon as={FaFacebook} boxSize={5} color="green.400" />
                                                <Link href="https://facebook.com/weunite" ml={3} isExternal>
                                                    Facebook: /weunite
                                                </Link>
                                            </Flex>
                                        </ListItem>
                                        <ListItem>
                                            <Flex align="center">
                                                <Icon as={FaInstagram} boxSize={5} color="green.400" />
                                                <Link href="https://instagram.com/weunite" ml={3} isExternal>
                                                    Instagram: @weunite
                                                </Link>
                                            </Flex>
                                        </ListItem>
                                        <ListItem>
                                            <Flex align="center">
                                                <Icon as={FaTwitter} boxSize={5} color="green.400" />
                                                <Link href="https://twitter.com/weunite" ml={3} isExternal>
                                                    Twitter: @weunite
                                                </Link>
                                            </Flex>
                                        </ListItem>
                                    </List>

                                    <Text mt={4}>Você também pode acessar nosso <Link color="green.400" href="/ajuda" isExternal>Centro de Ajuda</Link> para mais informações e artigos úteis.</Text>
                                </Box>
                            </TabPanel>
                        </TabPanels>
                    </Tabs>
                </ModalBody>
            </ModalContent>
        </Modal>
    );
};

export const TermsCheckbox = ({ onTermsAccepted, isChecked }) => {
    const { isOpen, onOpen, onClose } = useDisclosure();
    
    const handleCheckboxChange = (e) => {
        const newCheckedState = e.target.checked;
        onTermsAccepted?.(newCheckedState);
    };


    return (
        <Box>
            <Flex align="start">
                <Checkbox
                    colorScheme="green"
                    isChecked={isChecked}
                    onChange={handleCheckboxChange}
                    borderColor="#343434"
                    sx={{
                        '.chakra-checkbox__control[data-checked]': {
                            backgroundColor: '#03c03c !important',
                            borderColor: '#03c03c !important',
                        }
                    }}
                >
                    <Text fontSize="xs" color="#959595">
                        Ao preencher, você concorda com nossos{' '}
                        <Button
                            variant="link"
                            color="#03C03C"
                            fontSize="xs"
                            textDecoration="none"
                            onClick={onOpen}
                            _hover={{ textDecoration: 'none' }}
                        >
                            Termos, Política de Privacidade e Política de Cookies
                        </Button>
                    </Text>
                </Checkbox>
            </Flex>
            <TermsModal isOpen={isOpen} onClose={onClose} />
        </Box>
    );
};

export default TermsCheckbox;
