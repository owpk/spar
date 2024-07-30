import api from "../utils/api";
import { Endpoints } from "../constants";
import { CreatePersonalCouponsType, PersonalCouponsType } from "../types";
import { Versions } from "../config";

export class PersonalCouponsService {
  /**
   *
   * Request for get Catalogs Pages
   * @param data { offset, limit}
   */
  static async getPersonalCoupons(data: {
    offset?: number;
    limit?: number;
  }): Promise<Array<PersonalCouponsType>> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.PERSONAL_COUPONS}?${query.join("&")}`
    );


    return response.data.data;
  }
  /**
   * Request for get Catalogs Pages on id
   * @param id numder
   */
  static async getPersonalCouponsById(
    id: number
  ): Promise<PersonalCouponsType> {
    const response = await api.get(`${Endpoints.PERSONAL_COUPONS}/${id}`);
    return response.data.data;
  }

  /**
   *  Request for post Catalogs Pages
   * @param data {alias, title,url}
   */

  static async createPersonalCoupons(
    data: CreatePersonalCouponsType
  ): Promise<PersonalCouponsType> {
    const sendData = {
      data,
      version: Versions.PROFILE_COUPONS
    };
    const response = await api.put(Endpoints.PERSONAL_COUPONS, sendData);
    return response.data.data;
  }

  /**
     * Request for update Outside Docs on id
     * @param id number
     * @param data { alias, title, url}

     */
  static async updatePersonalCoupons(
    id: number,
    data: CreatePersonalCouponsType
  ): Promise<PersonalCouponsType> {
    const sendData = {
      data,
      version: Versions.PROFILE_COUPONS
    };
    const response = await api.put(
      `${Endpoints.PERSONAL_COUPONS}/${id}`,
      sendData
    );
    return response.data.data;
  }

  /**
   * Reques for delete Outsude Docs on id
   * @param id number
   */

  static async deletePersonalCoupons(id: number): Promise<boolean> {
    const response = await api.delete(`${Endpoints.PERSONAL_COUPONS}/${id}`);
    return response.data.success;
  }

  /**
   * Request for upload photo
   */
  static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
    try {
      const response = await api.post(`${Endpoints.PERSONAL_COUPONS}/${id}/photo`, file)

      return true
    } catch (error) {
      return false
    }
  }
}
