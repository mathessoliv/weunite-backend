import { modalAnatomy as parts } from '@chakra-ui/anatomy'
import { createMultiStyleConfigHelpers } from '@chakra-ui/styled-system'

const { definePartsStyle, defineMultiStyleConfig } =
    createMultiStyleConfigHelpers(parts.keys)

const baseStyle = definePartsStyle({
    dialog: {
        bg: "#000000",
        border: "1px solid #343434"
    },

    footer: {
        
    }
})

export const modalTheme = defineMultiStyleConfig({
    baseStyle,
})