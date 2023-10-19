import { useMutation } from "@tanstack/react-query"
import { updateMemberStatusService } from "../services/memberService"
import { queryClient } from ".."
import { axiosErrorType } from "../types/axiosErrorType"
import { useToken } from "./useToken"

export const useMemberStatusUpdate = (onSuccessHandler:Function) => {
    const refreshToken = useToken();
    const { mutate, isLoading, isError, error, isSuccess }
        = useMutation({
            mutationFn : updateMemberStatusService,
            onSuccess : (data) => {
                queryClient.invalidateQueries({ queryKey : ['members'] });
                onSuccessHandler();
            },
            onError : ( err : axiosErrorType ) => {
                if(err.response?.data.message==='만료된 토큰입니다.') {
                    refreshToken();
                } else {
                    console.log(err.response?.data.message);
                }
            }
        });
    
    return { mutate, isLoading, isError, error, isSuccess };
}