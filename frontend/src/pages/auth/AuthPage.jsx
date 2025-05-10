import { useRecoilValue } from 'recoil'; // Importa o hook useRecoilValue do Recoil para acessar o estado global
import Login from '../../components/auth/login/Login'; // Importa o componente de Login
import SignUpCard from '../../components/auth/signup/SignupCard'; // Importa o componente de cadastro
import authScreenAtom from '../../atoms/authAtom'; // Importa o átomo que armazena o estado da tela de autenticação
import CompanySignUpCard from '../../components/auth/signup/CompanySignUpCard';

const AuthPage = () => {
    // Obtém o estado atual da tela de autenticação do átomo global
    const authScreenState = useRecoilValue(authScreenAtom);
    
    // Log para depuração: exibe o estado atual da tela de autenticação
    console.log(authScreenState);

    return (
        <>
            {authScreenState === "login" && <Login />}
            {authScreenState === "signup" && <SignUpCard />}
            {authScreenState === "companysignup" && <CompanySignUpCard />}
        </>
    );
}

export default AuthPage; // Exporta o componente AuthPage para ser utilizado em outras partes da aplicação
