import { createContext, Dispatch } from "react";
import { AttributesType } from "../../../../types";

export const AttributesContext = createContext<[Array<AttributesType>, Dispatch<React.SetStateAction<AttributesType[]>>]>([[] , () => {}])
