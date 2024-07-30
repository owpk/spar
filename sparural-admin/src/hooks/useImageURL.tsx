import React, { useCallback, useEffect, useState } from 'react'

export const useImageURL = (link: string): string => {
    const [imageUrl, setImageUrl] = useState<string>('')

    const getImage = useCallback(async () => {
        let url = `/files/${link}`;
        if(!url) return
        const response = await fetch(url,{
            headers: {"x-client-type": "web", "Sec-Fetch-Mode": "no-cors"},
        }, );
        if (!response.ok) {
            // throw new Error('Ответ сети был не ok.');
        }
        const myBlob = await response.blob();
        const objectURL = URL.createObjectURL(myBlob);
        // imageRef.current.src = objectURL;
        setImageUrl(objectURL)
       
    }, [link])

    useEffect(() => {
        if ( link) {
            getImage()
        }
    }, [getImage, link])

    return imageUrl
}