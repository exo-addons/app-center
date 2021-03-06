<template>
  <div class="generalParams">
    <div class="form-container appCenter-form">
      <div class="maxFavoriteApps">
        <span>{{ $t("appCenter.adminSetupForm.maxFavoriteApps") }}</span>
        <span v-if="isMaxFavoriteAppsView && maxFavoriteApps !== ''">
          {{ maxFavoriteApps }}
        </span>

        <input
          v-if="!isMaxFavoriteAppsView"
          v-model="maxFavoriteApps"
          type="number"
          min="0"
          onkeypress="return event.charCode >= 48 && event.charCode <= 57">

        <a
          v-if="isMaxFavoriteAppsView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="isMaxFavoriteAppsView = false">
          <i class="uiIconEdit uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.edit")
          }}</span>
        </a>
        <a
          v-if="!isMaxFavoriteAppsView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="setMaxFavoriteApps()">
          <i class="uiIconSave uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.save")
          }}</span>
        </a>
        <a
          v-if="!isMaxFavoriteAppsView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="isMaxFavoriteAppsView = true">
          <i class="uiIconClose uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.cancel")
          }}</span>
        </a>
      </div>

      <div class="defaultAppImage">
        <span>{{ $t("appCenter.adminSetupForm.defaultAppImage") }}</span>
        <img
          v-if="defaultAppImage.isView && defaultAppImage.fileBody !== ''"
          class="appImage"
          :src="`data:image/png;base64,${defaultAppImage.fileBody}`">

        <label
          v-if="!defaultAppImage.isView"
          for="defaultAppImageFile"
          class="custom-file-upload">
          <i class="uiDownloadIcon download-icon"></i>{{ $t("appCenter.adminSetupForm.browse") }}
        </label>
        <input
          v-if="!defaultAppImage.isView"
          id="defaultAppImageFile"
          ref="defaultAppImageFile"
          type="file"
          accept="image/*"
          @change="handleDefaultAppImageFileUpload()">
        <div
          v-if="
            !defaultAppImage.isView &&
              !defaultAppImage.fileName &&
              !defaultAppImage.fileName
          "
          class="file-listing">
          {{ defaultAppImage.fileName }}
          <span class="remove-file" @click="removeDefaultAppImageFile()">
            <i class="uiCloseIcon"></i>
          </span>
        </div>
        <a
          v-if="defaultAppImage.isView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="defaultAppImage.isView = false">
          <i class="uiIconEdit uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.edit")
          }}</span>
        </a>
        <a
          v-if="!defaultAppImage.isView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="submitDefaultAppImage()">
          <i class="uiIconSave uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.save")
          }}</span>
        </a>
        <a
          v-if="!defaultAppImage.isView"
          class="actionIcon tooltipContent"
          data-placement="bottom"
          data-container="body"
          @click.stop="resetDefaultAppImage()">
          <i class="uiIconClose uiIconLightGray"></i>
          <span class="tooltiptext tooltiptextIcon">{{
            $t("appCenter.adminSetupForm.cancel")
          }}</span>
        </a>
        <p
          :class="
            'errorInput' + (defaultAppImage.invalidSize ? '' : ' sizeInfo')
          ">
          <img
            width="13"
            height="13"
            src="/app-center/skin/images/Info tooltip.png">
          {{ $t("appCenter.adminSetupForm.sizeError") }}
        </p>
        <p v-if="defaultAppImage.invalidImage" class="errorInput">
          {{ $t("appCenter.adminSetupForm.imageError") }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AdminSetupGeneralParams",
  data() {
    return {
      maxFavoriteApps: 0,
      isMaxFavoriteAppsView: true,
      defaultAppImage: {
        fileBody: "",
        fileName: "",
        isView: true,
        invalidSize: false,
        invalidImage: false
      }
    };
  },
  created() {
    this.getAppGeneralSettings();
  },
  methods: {
    getAppGeneralSettings() {
      return fetch("/portal/rest/app-center/settings", {
        method: "GET"
      })
        .then(resp => {
          if (resp && resp.ok) {
            return resp.json();
          } else {
            throw new Error("Error getting favorite applications list");
          }
        })
        .then(data => {
          this.maxFavoriteApps = data && data.maxFavoriteApps;
          Object.assign(this.defaultAppImage, data && data.defaultApplicationImage);
        });
    },

    setMaxFavoriteApps() {
      this.$nextTick()
      .then(fetch(`/portal/rest/app-center/settings/maxFavorites?number=${this.maxFavoriteApps}`, {
        method: "PATCH"
      })).then(() => {
        this.isMaxFavoriteAppsView = true;
      });
    },

    submitDefaultAppImage() {
      if (
        this.defaultAppImage.fileBody === "" &&
        this.defaultAppImage.fileName === ""
      ) {
        this.setDefaultAppImage();
      } else {
        if (this.$refs.defaultAppImageFile.files.length > 0) {
          const reader = new FileReader();
          reader.onload = e => {
            if (!e.target.result.includes("data:image")) {
              this.defaultAppImage.invalidImage = true;
              return;
            }
            if (this.$refs.defaultAppImageFile.files[0].size > 100000) {
              this.defaultAppImage.invalidSize = true;
              return;
            }
            this.defaultAppImage.fileBody = e.target.result;
            this.setDefaultAppImage();
          };
          reader.readAsDataURL(this.$refs.defaultAppImageFile.files[0]);
        } else {
          this.setDefaultAppImage();
        }
      }
    },

    setDefaultAppImage() {
      const setDefaultAppImageUrl = "/portal/rest/app-center/settings/image";
      return fetch(setDefaultAppImageUrl, {
        method: "PATCH",
        credentials: "include",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          id: this.defaultAppImage && this.defaultAppImage.id,
          fileName: this.defaultAppImage && this.defaultAppImage.fileName,
          fileBody: this.defaultAppImage && this.defaultAppImage.fileBody
        })
      })
        .then(() => {
          this.defaultAppImage.isView = true;
          this.getAppGeneralSettings();
        })
        .catch(e => {
          throw new Error(
            `Error when setting the default application image ${e}`
          );
        });
    },

    handleDefaultAppImageFileUpload() {
      if (this.$refs.defaultAppImageFile.files.length > 0) {
        this.defaultAppImage.fileName = this.$refs.defaultAppImageFile.files[0].name;
        this.defaultAppImage.invalidSize = false;
        this.defaultAppImage.invalidImage = false;
      } else {
        this.removeDefaultAppImageFile();
      }
    },

    removeDefaultAppImageFile() {
      this.defaultAppImage.fileName = "";
      this.defaultAppImage.fileBody = "";
      this.defaultAppImage.invalidSize = false;
      this.defaultAppImage.invalidImage = false;
    },

    resetDefaultAppImage() {
      this.defaultAppImage.isView = true;
      this.defaultAppImage.invalidSize = false;
      this.defaultAppImage.invalidImage = false;
      this.getAppGeneralSettings();
    }
  }
};
</script>
