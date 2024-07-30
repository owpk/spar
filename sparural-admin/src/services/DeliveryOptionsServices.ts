import api from "../utils/api";
import { Endpoints } from "../constants";
import {
  BannerScreenType,
  CreateBannerPlaceType,
  CreateInfoScreen,
  DeliveryCreateType,
  DeliveryType
} from "../types";
import { bannersDemo, DeliveryDemoData } from "../demoData";
import { Versions } from "../config";

export class DeliveryOptionsServices {
  /**
   * Request for get BannerPlace
   */
  static async getDelivery(data: {
    offset?: number;
    limit?: number;
  }): Promise<DeliveryType[]> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.DELIVERY_OPTIONS}?${query.join("&")}`
    );

    return response.data.data;
  }

  /**
   * Request for get BannerPlace by id
   */
  static async getDeliveryById(id: number): Promise<DeliveryType> {
    const response = await api.get(`${Endpoints.DELIVERY_OPTIONS}/${id}`);
    return response.data.data;
  }

  /**
   * Request for create BannerPlace
   */
  static async createBannerPlace(
    data: DeliveryCreateType
  ): Promise<DeliveryType> {
    const sendData = {
      data,
      version: Versions.DELIVERY_OPTIONS
    };
    const response = await api.post(Endpoints.DELIVERY_OPTIONS, sendData);
    return response.data.data;
  }

  /**
   * Request for update BannerPlace
   */
  static async updateDeliveryPlace(
    id: number,
    data: DeliveryCreateType
  ): Promise<DeliveryType> {
    const sendData = {
      data,
      version: Versions.DELIVERY_OPTIONS
    };
    const response = await api.put(
      `${Endpoints.DELIVERY_OPTIONS}/${id}`,
      sendData
    );
    return response.data.data;
  }

  /**
   * Request for delete BannerPlace
   */
  static async deleteBannerPlace(id: number): Promise<boolean> {
    const response = await api.delete(`${Endpoints.DELIVERY_OPTIONS}/${id}`);

    return response.data.success;
  }

  /**
   * Request for upload photo
   */
   static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
    try {
        const response = await api.post(`${Endpoints.DELIVERY_OPTIONS}/${id}/photo`, file)
        return true
    } catch (error) {
        return false
    }
}
}
