import { AxiosRequestConfig, AxiosResponse } from "axios";

export type axiosErrorType = {
    config: AxiosRequestConfig<any>;
    code?: string;
    request?: any;
    response?: AxiosResponse<any, any>;
    isAxiosError: boolean;
    toJSON: () => object;
}

export type errorResponseDataType = {
    message: string;
    code: number;
}