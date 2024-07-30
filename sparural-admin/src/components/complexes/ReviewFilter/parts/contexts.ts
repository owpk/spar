import React, { createContext } from "react";

export type ReviewFilterType = {
    search?: string
    grade?: Array<number>
    dateTimeStart?: number
    dateTimeEnd ?: number
    merchantId?: Array<number>
}

export const FilterContext = createContext<[ReviewFilterType, React.Dispatch<React.SetStateAction<ReviewFilterType>>]>([{}, () => {}])