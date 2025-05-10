import { atom } from 'recoil'

const commentsAtom = atom({
    key: 'commentsAtom',
    default: []
});

export default commentsAtom;