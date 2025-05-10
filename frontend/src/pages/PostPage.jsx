import { Avatar, Box, Divider, Flex, IconButton, Image, Link, Spinner, Text, useColorMode } from "@chakra-ui/react";
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import useGetUserProfile from "../hooks/useGetUserProfile";
import { useEffect } from "react";
import useShowToast from "../hooks/useShowToast";
import Actions from "../components/videos/Actions";
import { formatDistanceToNow } from "date-fns";
import { useRecoilState, useRecoilValue } from "recoil";
import userAtom from "../atoms/userAtom";
import { DeleteIcon } from "@chakra-ui/icons";
import Comment from "../components/videos/Comment";
import postsAtom from "../atoms/postsAtom";
import { IoMdArrowRoundBack } from "react-icons/io";
import CustomVideoPlayer from "../components/videos/CustomVideoPlayer";

const PostPage = () => {
  const { user, loading } = useGetUserProfile();
  const [posts, setPosts] = useRecoilState(postsAtom);
  const showToast = useShowToast();
  const { pid } = useParams();
  const navigate = useNavigate();
  const { colorMode } = useColorMode();
  const currentUser = useRecoilValue(userAtom);

  const currentPost = posts[0];

  useEffect(() => {
    const getPost = async () => {
      setPosts([]);
      try {
        const res = await fetch(`/api/posts/${pid}`);
        const data = await res.json();
        if (data.error) {
          showToast("Error", data.error, "error");
          return;
        }
        setPosts([data]);
      } catch (error) {
        showToast("Error", error.message, "error");
      }
    };
    getPost();
  }, [showToast, pid, setPosts]);

  const handleDeletePost = async () => {
    try {
      if (!window.confirm("Are you sure you want to delete this post?")) return;

      const res = await fetch(`/api/posts/${currentPost._id}`, {
        method: "DELETE",
      });
      const data = await res.json();
      if (data.error) {
        showToast("Erro", data.error, "error");
        return;
      }
      showToast("Success", "Post deleted", "success");
      navigate(`/${user.username}`);
    } catch (error) {
      showToast("Erro", error.message, "error");
    }
  };

  const handleCommentDeleted = (deletedCommentId) => {
    setPosts((prevPosts) => {
      const updatedPost = {
        ...prevPosts[0],
        replies: prevPosts[0].replies.filter((reply) => reply._id !== deletedCommentId),
      };
      return [updatedPost];
    });
  };

  const renderMedia = () => {
    if (currentPost.mediaType === 'image' && currentPost.img) {
      return (
        <Image
          objectFit='cover'
          src={currentPost.img}
          alt='Post Image'
          borderRadius={5}
          mt={4}
        />
      );
    } else if (currentPost.mediaType === 'video' && currentPost.video) {
      return (
        <Box mt={4} width="100%">
          <CustomVideoPlayer src={currentPost.video} />
        </Box>
      );
    }
    return null;
  };

  if (!user && loading) {
    return (
      <Flex justifyContent={"center"} alignItems={"center"} minH={"100vh"}>
        <Spinner size={"xl"} />
      </Flex>
    );
  }

  if (!currentPost) return null;

  return (
    <Flex w={"100vw"} h={"100vh"} justifyContent={"center"}>
      <Flex w={{ base: "100vw", md: "50vw" }} flexDir={"column"}>
        <Box display={{ base: "none", md: "flex" }} justifyContent={"center"} p={{ base: 4 }}>
          <Link as={RouterLink} to={"/"}>
            <IconButton bg={"transparent"} icon={<IoMdArrowRoundBack size={26} cursor={"pointer"} />} />
          </Link>
        </Box>

        <Flex 
          width={"100%"} 
          flexDirection={"column"} 
          bg={colorMode === "dark" ? "#000000" : "gray.100"}
          px={5} 
          py={2} 
          mt={{ base: 0, md: 5 }}
          borderRadius="lg" 
          shadow="md"
          border={colorMode === "dark" ? "1px solid #343434" : ""}
        >
          <Box display={{ base: "flex", md: "none" }} justifyContent={"left"} mb={1}>
            <Link as={RouterLink} to={"/"}>
              <IconButton bg={"transparent"} icon={<IoMdArrowRoundBack size={26} cursor={"pointer"} />} />
            </Link>
          </Box>

          <Flex justifyContent={"space-between"} flex={1}>
            <Flex alignItems={"center"} gap={3}>
              <Avatar
                src={user?.profilePic}
                name={user?.username}
                onClick={(e) => {
                  e.preventDefault();
                  navigate(`/${user.username}`);
                }}
                cursor={"pointer"}
              />
              <Text
                fontWeight={"bold"}
                onClick={(e) => {
                  e.preventDefault();
                  navigate(`/${user?.username}`);
                }}
                cursor={"pointer"}
              >
                {user?.username}
              </Text>
            </Flex>

            <Flex alignItems={"center"} justifyContent={"center"} mr={4} gap={2}>
              <Flex>
                <Text color={"#959595"}>
                  {formatDistanceToNow(new Date(currentPost.createdAt))} ago
                </Text>
              </Flex>

              {currentUser?._id === user?._id && (
                <Flex>
                  <DeleteIcon size={20} onClick={handleDeletePost} cursor={"pointer"} />
                </Flex>
              )}
            </Flex>
          </Flex>

          <Flex mt={4} flexDirection={"column"}>
            <Text>{currentPost.text}</Text>
            {renderMedia()}
          </Flex>
          <Actions post={currentPost} />

          <Divider mt={4} />
          {currentPost.replies.map(reply => (
            <Comment
              key={reply._id}
              reply={reply}
              lastReply={reply._id === currentPost.replies[currentPost.replies.length - 1]._id}
              post={currentPost}
              currentUser={currentUser}
              onCommentDeleted={handleCommentDeleted}
            />
          ))}
        </Flex>
      </Flex>
    </Flex>
  );
};

export default PostPage;
