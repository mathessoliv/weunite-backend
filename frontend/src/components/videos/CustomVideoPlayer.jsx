import { useState, useRef, useEffect } from 'react';
import { Box, Flex, IconButton, Slider, SliderTrack, SliderFilledTrack, SliderThumb, Text,} from '@chakra-ui/react';
import { Play, Pause, Volume2, VolumeX, Maximize2, Minimize2, SkipBack, SkipForward } from 'lucide-react';

// eslint-disable-next-line react/prop-types
const CustomVideoPlayer = ({ src }) => {
    const [isPlaying, setIsPlaying] = useState(false);
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);
    const [volume, setVolume] = useState(1);
    const [isMuted, setIsMuted] = useState(false);
    const [isFullscreen, setIsFullscreen] = useState(false);
    const [showControls, setShowControls] = useState(true);
    const [isHovering, setIsHovering] = useState(false);
    const videoRef = useRef(null);
    const playerRef = useRef(null);
    let controlsTimeout = null;

    useEffect(() => {
      const video = videoRef.current;
      if (!video) return;

      const handleLoadedMetadata = () => {
        setDuration(video.duration);
      };

      video.addEventListener('loadedmetadata', handleLoadedMetadata);
      return () => video.removeEventListener('loadedmetadata', handleLoadedMetadata);
    }, []);

    useEffect(() => {
      const video = videoRef.current;
      if (!video) return;

      const handleTimeUpdate = () => {
        setCurrentTime(video.currentTime);
      };
      
      video.addEventListener('timeupdate', handleTimeUpdate);
      return () => video.removeEventListener('timeupdate', handleTimeUpdate);
    }, []);

    useEffect(() => {
      const handleFullscreenChange = () => {
        setIsFullscreen(!!document.fullscreenElement);
      };

      document.addEventListener('fullscreenchange', handleFullscreenChange);
      return () => document.removeEventListener('fullscreenchange', handleFullscreenChange);
    }, []);

    const formatTime = (timeInSeconds) => {
      const minutes = Math.floor(timeInSeconds / 60);
      const seconds = Math.floor(timeInSeconds % 60);
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    };

    const handlePlayPause = () => {
      if (videoRef.current.paused) {
        videoRef.current.play();
        setIsPlaying(true);
      } else {
        videoRef.current.pause();
        setIsPlaying(false);
      }
    };

    const handleVolumeChange = (newVolume) => {
      setVolume(newVolume);
      videoRef.current.volume = newVolume;
      setIsMuted(newVolume === 0);
    };

    const handleMuteToggle = () => {
      if (isMuted) {
        videoRef.current.volume = volume;
        setIsMuted(false);
        
      } else {
        videoRef.current.volume = 0;
        setIsMuted(true);
      }
    };

    const handleTimeChange = (newTime) => {
      videoRef.current.currentTime = newTime;
      setCurrentTime(newTime);
    };

    const handleSkip = (seconds) => {
      const newTime = Math.min(Math.max(currentTime + seconds, 0), duration);
      handleTimeChange(newTime);
    };

    const handleFullscreenToggle = () => {
      if (!isFullscreen) {
        if (playerRef.current.requestFullscreen) {
          playerRef.current.requestFullscreen();
        }

      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        }

      }
    };

    const handleMouseMove = () => {
      setShowControls(true);
      clearTimeout(controlsTimeout);
      controlsTimeout = setTimeout(() => {
        if (isPlaying && !isHovering) {
          setShowControls(false);
        }
      }, 3000);
    };

    return (

      <Box // posicionamento do player
        ref={playerRef}
        position="relative"
        width="100%"
        paddingTop="56.25%" // 16:9 Aspect Ratio
        bg="black"
        borderRadius="lg"
        overflow="hidden"
        boxShadow="2xl">

        <Box // posicionando o vídeo
          as="div"
          position="absolute"
          top="0"
          left="0"
          right="0"
          bottom="0"
          onMouseMove={handleMouseMove}
          onMouseEnter={() => setIsHovering(true)}
          onMouseLeave={() => {
            setIsHovering(false);
            if (isPlaying) setShowControls(false);
          }}>

          <Box // video
            as="video"
            ref={videoRef}
            src={src}
            onClick={handlePlayPause}
            width="100%"
            height="100%"
            objectFit="contain"
            cursor="pointer"/>

          {/* Controls Container */}
          <Flex
            position="absolute"
            bottom="0"
            left="0"
            right="0"
            direction="column"
            bg="blackAlpha.800"
            backdropFilter="blur(8px)"
            transform={showControls ? 'translateY(0)' : 'translateY(100%)'}
            transition="all 0.3s ease">

            {/* Progress Bar */}
            <Box px="3" pt="2">

              <Slider 
                value={currentTime}
                min={0}
                max={duration}
                onChange={handleTimeChange}
                focusThumbOnChange={false}
                size="sm">

                <SliderTrack bg="whiteAlpha.200" h="2px">
                  <SliderFilledTrack bg="#03C03C" />
                </SliderTrack>

                <SliderThumb boxSize={2} />
              </Slider>
            
            </Box>

            {/* Controls */}
            <Flex align="center" justify="space-between" px="3" py="2" gap="3">

              <Flex align="center" gap="2"> {/* controle do vídeo */}
                
                {/* voltar 10 segundos */}
                <IconButton
                  icon={<SkipBack size={16} />}
                  onClick={() => handleSkip(-10)}
                  variant="ghost"
                  color="white"
                  _hover={{ bg: 'whiteAlpha.300' }}
                  aria-label="Skip backward"
                  size="sm"/>

                {/* pausar/despausar vídeo */}
                <IconButton
                  icon={isPlaying ? <Pause size={16} /> : <Play size={16} />}
                  onClick={handlePlayPause}
                  variant="ghost"
                  color="white"
                  _hover={{ bg: 'whiteAlpha.300' }}
                  aria-label={isPlaying ? 'Pause' : 'Play'}
                  size="sm"/>

                {/* avançar 10 segundos */}
                <IconButton
                  icon={<SkipForward size={16} />}
                  onClick={() => handleSkip(10)}
                  variant="ghost"
                  color="white"
                  _hover={{ bg: 'whiteAlpha.300' }}
                  aria-label="Skip forward"
                  size="sm"/>

                <Flex align="center" gap="2"> {/* volume */}

                  {/* ícone de volume */}
                  <IconButton
                    icon={isMuted ? <VolumeX size={16} /> : <Volume2 size={16} />}
                    onClick={handleMuteToggle}
                    variant="ghost"
                    color="white"
                    _hover={{ bg: 'whiteAlpha.300' }}
                    aria-label={isMuted ? 'Unmute' : 'Mute'}
                    size="sm"/>

                  {/* controle de volume */}
                  <Box width="80px">        
                    <Slider
                      value={isMuted ? 0 : volume}
                      min={0}
                      max={1}
                      step={0.1}
                      onChange={handleVolumeChange}
                      focusThumbOnChange={false}
                      size="sm">
                      
                      <SliderTrack bg="whiteAlpha.200" h="2px">
                        <SliderFilledTrack bg="#03C03C" />
                      </SliderTrack>
                      
                      <SliderThumb boxSize={2} />

                    </Slider>
                  </Box> {/* encerramento controle de volume */}

                </Flex> {/* encerramento volume */}

                {/* tempo do vídeo */}
                <Text color="white" fontSize="xs" pl="10px">
                  {formatTime(currentTime)} / {formatTime(duration)}
                </Text>

              </Flex> {/* encerramento controle do vídeo */}

              <IconButton
                icon={isFullscreen ? <Minimize2 size={16} /> : <Maximize2 size={16} />}
                onClick={handleFullscreenToggle}
                variant="ghost"
                color="white"
                _hover={{ bg: 'whiteAlpha.300' }}
                aria-label={isFullscreen ? 'Exit fullscreen' : 'Enter fullscreen'}
                size="sm"/>

            </Flex> {/* encerramento controles */}

          </Flex> {/* encerramento do container de controles */}

        </Box> {/* encerramento do posicionamento do vídeo */}
      
      </Box> // encerramento das posicionamento do player
    );
  };

export default CustomVideoPlayer;