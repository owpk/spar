import axios, { AxiosRequestConfig } from "axios";
import createAuthRefreshInterceptor from "axios-auth-refresh";
import { AuthService } from "../services/AuthService";

//https://www.npmjs.com/package/axios-auth-refresh
export type ErrorsTypes = {
  [field: string]: Array<string> | Array<{ [index: number]: Array<string> }>;
};

export type ResponseType = {
  code: number;
  success: boolean;
  data?: any;
  errors?: ErrorsTypes;
};

// Function that will be called to refresh authorization
const refreshAuthLogic = () =>
  AuthService.refreshToken().then(() => {
    return Promise.resolve();
  });

const config: AxiosRequestConfig = {
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
    "x-client-type": "web"
  }
};

/**
 * _PREFIX_URL
 */

const _PREFIX_URL = "/api/v1";
config.baseURL = _PREFIX_URL;
const instance = axios.create(config);

// Устанавливаем токен в заголовок при каждом запросе
instance.interceptors.request.use((request: any) => {
  return request;
});

// Instantiate the interceptor
createAuthRefreshInterceptor(instance, refreshAuthLogic, {
  statusCodes: [401, 403]
});

export default instance;
