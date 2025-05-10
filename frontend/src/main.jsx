import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { ChakraProvider, ColorModeScript } from '@chakra-ui/react'

import { BrowserRouter } from 'react-router-dom'
import { theme } from './components/styles/theme.js'
import { RecoilRoot } from 'recoil'
import { GoogleOAuthProvider } from '@react-oauth/google';
import { SocketContextProvider } from './context/SocketContext.jsx'



ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>

    <RecoilRoot>
      <BrowserRouter>
        <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
          <ChakraProvider theme={theme}>
            <ColorModeScript initialColorMode={theme.config.initialColorMode} />
            <SocketContextProvider>
              <App />
            </SocketContextProvider>
          </ChakraProvider>
        </GoogleOAuthProvider>
      </BrowserRouter>
    </RecoilRoot>
  </React.StrictMode>,
)

