import api from "../utils/api";
import { Endpoints } from "../constants";
import {
  BannerScreenType,
  CreateBannerPlaceType,
  CreateInfoScreen,
  MobileNavigateTargetType
} from "../types";
import { bannersDemo } from "../demoData";
import { Versions } from "../config";

export class BannerPlaceService {
  /**
   * Request for get BannerPlace
   */
  static async getBannerPlaces(data: {
    offset?: number;
    limit?: number;
  }): Promise<Array<BannerScreenType>> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.BANNER_PLACE}?${query.join("&")}`
    );
    return response.data.data;
    // !TODO delete when backend wil be ready
    // return bannersDemo
  }
  /**
   * Request for get Selectorscreens
   */
  static async getSelectDropDown(data: {
    offset?: number;
    limit?: number;
  }): Promise<Array<MobileNavigateTargetType>> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.SELECT_SCREENS}?${query.join("&")}`
    );
    return response.data.data;
    // !TODO delete when backend wil be ready
    // return bannersDemo
  }

  /**
   * Request for get BannerPlace by id
   */
  static async getBannerPlaceById(id: number): Promise<BannerScreenType> {
    const response = await api.get(`${Endpoints.BANNER_PLACE}/${id}`);
    return response.data.data;
    // !TODO delete when backend wil be ready
    // return bannersDemo[0]
  }

  /**
   * Request for update BannerPlace
   */
  static async updateBannerPlace(
    id: number,
    data: CreateBannerPlaceType
  ): Promise<BannerScreenType> {
    const sendData = {
      data,
      version: Versions.BANNER_PLACE
    };
    const response = await api.put(`${Endpoints.BANNER_PLACE}/${id}`, sendData);
    return response.data.data;
  }

  /**
   * Request for create BannerPlace
   */
  static async createBannerPlace(
    data: CreateBannerPlaceType
  ): Promise<BannerScreenType> {
    const sendData = {
      data,
      version: Versions.BANNER_PLACE
    };
    const response = await api.post(Endpoints.BANNER_PLACE, sendData);
    return response.data.data;
  }

  /**
   * Request for delete BannerPlace
   */
  static async deleteBannerPlace(id: number): Promise<boolean> {
    const response = await api.delete(`${Endpoints.BANNER_PLACE}/${id}`);

    return response.data.success;
  }

      /**
     * Request for upload photo
     */
       static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(`${Endpoints.BANNER_PLACE}/${id}/photo`, file)
            return true
        } catch (error) {
            return false
        }
    }
}
