import { useEffect, useState } from 'react'
import useShowToast from './useShowToast';
import userAtom from '../atoms/userAtom';
import { useRecoilValue } from 'recoil';

const useGetUserProfile = () => {
    const userAtomo = useRecoilValue(userAtom);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const showToast = useShowToast();

    useEffect(() => {
        const getUser = async () => {
            try {
                const res = await fetch(`/api/users/profile/${userAtomo.username}`)
                const data = await res.json()
                if (data.error) {
                    showToast("Error", data.error, "error");
                    return;
                }
                setUser(data);
            } catch (error) {
                showToast("Error", error.message, "error");
            } finally {
                setLoading(false);
            }
        };

        getUser();
    }, [userAtomo.username, showToast])
    return { loading, user }
}

export default useGetUserProfile