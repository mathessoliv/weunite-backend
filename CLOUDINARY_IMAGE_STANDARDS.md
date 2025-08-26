# Sistema de Imagens para Cloudinary - WeUnite

Este documento descreve o sistema de upload e processamento de imagens implementado para o sistema WeUnite.

## Tipos de Imagem Suportados

O sistema suporta apenas 3 tipos de imagem essenciais:

### 📸 **Imagem de Perfil**
- **Tamanho**: 400x400px (sempre quadrada)
- **Exibição**: Circular no frontend via CSS (`border-radius: 50%`)
- **Crop**: `fill` com `gravity: face` (foca no rosto)
- **Pasta**: `profile/{username}/`
- **Tags**: `profile, img, circular`

### 🎨 **Banner de Perfil**
- **Altura**: 300px (largura proporcional)
- **Crop**: `fill` com `gravity: center`
- **Pasta**: `banner/{username}/`
- **Tags**: `profile, img, banner`

### 📱 **Posts** (Sistema Adaptativo)
O sistema detecta automaticamente a orientação e aplica transformações específicas:

#### Imagens Horizontais (largura > altura)
- **Largura máxima**: 1280px
- **Altura máxima**: 720px
- **Comportamento**: Ocupam toda largura disponível
- **Tags**: `post, user_content, horizontal`

#### Imagens Verticais (altura ≥ largura)
- **Largura máxima**: 375px
- **Altura máxima**: 500px
- **Comportamento**: Tamanho controlado para não dominar o feed
- **Tags**: `post, user_content, vertical`

## Configurações

```properties
# Cloudinary Image Standards
cloudinary.image.profile.height=400
cloudinary.image.banner.height=300
cloudinary.image.post.height=720
cloudinary.image.post.width=1280
cloudinary.image.post.vertical-max-height=500
cloudinary.image.post.vertical-max-width=375
```

## Lógica de Detecção para Posts

```java
double aspectRatio = (double) originalWidth / originalHeight;

if (aspectRatio > 1.0) {
    // HORIZONTAL: dimensões completas (1280x720)
} else {
    // VERTICAL: limites reduzidos (375x500)
}
```

## Métodos Disponíveis

### CloudinaryService

- `uploadPost(MultipartFile file, Long userId)` - Upload inteligente de posts
- `uploadProfileImg(MultipartFile file, String username)` - Upload de foto de perfil
- `uploadBannerImg(MultipartFile file, String username)` - Upload de banner
- `getImageWithHeight(String publicId, int height)` - URL com altura customizada

## Exemplos Práticos

### Post Horizontal (ex: 1920x1080)
- **Entrada**: 1920x1080
- **Saída**: 1280x720 (ocupa largura total)

### Post Vertical (ex: 1200x1600) 
- **Entrada**: 1200x1600
- **Saída**: 375x500 (tamanho controlado)

### Perfil (qualquer tamanho)
- **Entrada**: Qualquer proporção
- **Saída**: 400x400 (quadrada para exibição circular)

### Banner (qualquer tamanho)
- **Entrada**: Qualquer proporção
- **Saída**: 300px altura, largura proporcional

## Vantagens do Sistema

- ✅ **Simplicidade**: Apenas 3 tipos essenciais
- ✅ **Adaptativo**: Posts se ajustam automaticamente à orientação
- ✅ **Qualidade**: Horizontais em alta resolução, verticais controladas
- ✅ **Performance**: Otimização automática de qualidade e formato
- ✅ **Organização**: Estrutura de pastas clara por tipo e usuário

## Estrutura de Pastas

```
cloudinary/
├── posts/{userId}/          # Imagens de posts
├── profile/{username}/      # Fotos de perfil  
└── banner/{username}/       # Banners de perfil
```

Este sistema garante que cada tipo de imagem tenha o tratamento adequado para sua função específica na aplicação.
