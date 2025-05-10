import { Container } from "@chakra-ui/react";
import PostPage from "./pages/PostPage";
import UserPage from "./pages/user/UserPage";
import { Navigate, Route, Routes } from "react-router-dom";
import AuthPage from "./pages/auth/AuthPage";
import HomePage from "./pages/HomePage";
import UpdateProfilePage from "./pages/user/UpdateProfilePage";
import CreatePost from "./components/user/CreatePost";
import CreateOportunity from "./components/oportunities/CreateOportunity";
import ChatPage from "./pages/ChatPage";
import OportunityPage from "./pages/oportunity/OportunityPage";
import ForgotPasswordPage from "./pages/auth/ForgotPasswordPage";
import ResetPasswordPage from "./pages/auth/ResetPasswordPage";
import VerifyResetCodePage from "./pages/auth/VerifyResetCodePage";
import VerifyEmailPage from "./pages/auth/VerifyEmailPage";
import OportunityDetailsPage from "./pages/oportunity/OportunityDetailsPage";
import SavedOportunitiesPage from "./pages/oportunity/SavedOportunitiesPage";
import { useRecoilValue } from "recoil";
import userAtom from "./atoms/userAtom";
import MyApplicationsPage from "./pages/MyApplicationsPage";
import ClubOpportunitiesPage from "./pages/oportunity/ClubOpportunitiesPage";
import OpportunityApplicationsPage from "./pages/oportunity/OpportunityApplicationsPage";


function App() {
    const user = useRecoilValue(userAtom);
    console.log(user);
    return (
        <Container maxW={"full"} p={0} overflowX={"hidden"} >

            <Routes>
                <Route path="/auth" element={!user ? <AuthPage /> : <Navigate to={"/"} />} />
                <Route path="/" element={user ? <HomePage /> : <Navigate to="/auth" />} />
                <Route path="/update" element={user ? <UpdateProfilePage /> : <Navigate to="/auth" />} />

                <Route path="/:username" element={user ? (
                    <>
                        <UserPage />
                        <CreatePost />
                    </>
                ) : (
                    <UserPage />
                )} />
                <Route path="/:username/post/:pid" element={<PostPage />} />
                <Route path="/chat" element={user ? <ChatPage /> : <Navigate to={"/auth"} />} />
                {/* <Route path="/oportunities" element={user ? 
                <OportunityPage /> : <Navigate to={"/auth"} />} /> */}
                <Route path="/reset-password/:token" element={<ResetPasswordPage />} />
                <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                <Route path="/verify-reset-code" element={<VerifyResetCodePage />} />
                <Route path="/verify-email" element={<VerifyEmailPage />} />

                <Route path="/oportunities" element={user && user.userType === "Clube" ? (
                    <>
                        <OportunityPage />
                        <CreateOportunity />
                    </>
                ) : (
                    <OportunityPage />
                )} />
                <Route path="/saved-oportunities/:username" element={<SavedOportunitiesPage />} />
                <Route path="/:username/oportunities/:oid" element={<OportunityDetailsPage />} />
                <Route 
                    path="/:username/applied-oportunities" 
                    element={user ? <MyApplicationsPage /> : <Navigate to="/auth" />} 
                />
                       <Route 
                    path="/my-oportunities" 
                    element={user && user.userType === "Clube" ? <ClubOpportunitiesPage /> : <Navigate to="/" />} 
                />
                <Route 
                    path="/oportunities/:oid/applicants" 
                    element={user && user.userType === "Clube" ? <OpportunityApplicationsPage /> : <Navigate to="/" />} 
                />

            </Routes>

            {/* {user && <LogoutButton />} */}

        </Container>
    );
}

export default App;
