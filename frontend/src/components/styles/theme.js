import { extendTheme} from '@chakra-ui/react'
import { mode } from "@chakra-ui/theme-tools";
import { menuTheme } from './Menu';
import { modalTheme } from './Modal';
import '@fontsource-variable/inter'

const config = {
    initialColorMode: 'light',
    useSystemColorMode: true,
}

const styles = {
    global: (props) => ({
        body: {
            color: mode("gray.800", "whiteAlpha.900")(props),
            bg: mode("gray.200", "#0A0A0A")(props)
        },
    }),
};

const breakpoints = {
    base: '300px', // 0px
    sm: '500px', // ~480px. em is a relative unit and is dependant on the font-size.
    md: '700px', // ~768px
    lg: '1400px', // ~992px
  }

export const theme = extendTheme({
    config,
    styles,
    components: {
        Menu: menuTheme,
        Modal: modalTheme,
    },
    fonts: {
        heading: `'Inter Variable', sans-serif`,
        body: `'Inter Variable', sans-serif`
    },
    breakpoints
})



